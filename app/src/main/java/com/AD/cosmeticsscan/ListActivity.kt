package com.AD.cosmeticsscan

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import android.R.id
import android.content.Context
import android.content.DialogInterface
import android.widget.EditText
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Tasks.call
import io.reactivex.Observable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_main.*

class ListActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    private var disposable2: Disposable? = null
    private val baseURL = "http://192.168.0.17:8000"
    private var currentCosmetic = mutableListOf<com.AD.cosmeticsscan.Fields>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val linearLayout = findViewById<LinearLayout>(R.id.linear)

        val service = Retrofit.Builder()
            .baseUrl("https://public.opendatasoft.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CosmeticsIngredientsService::class.java)

        val extras = intent.extras
        if (extras != null) {
            val ingredientsList = extras.getString("ingredients_list")

            if (ingredientsList!= null) {
                //prepare text block to use in API calls
                val ingredientList = ingredientsList.removePrefix("INGREDIENTS")
                val iList = ingredientList.replace("\\s".toRegex(), " ")
                val elements = iList.split(",")
                // loop over every element of the ingredients list to call for the description
                var i = 0
                for((number, element)in elements.withIndex()){
                    i++
                    // TODO: sort ingredients in previous order
                    // TODO: check if restriction is present and if is print it as well
                    // TODO: add buttons to set ingredient as favourite
                    disposable = service.checkIngredient("cosmetic-ingredient-database-ingredients-and-fragrance-inventory",element, 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { result ->
                                val tvdynamic = TextView(this)
                                tvdynamic.textSize = 20f
                                tvdynamic.text = getString(R.string.ingredient_description, (number+1).toString(), element.toLowerCase(), result.records[0].fields.function.toLowerCase())
                                linearLayout.addView(tvdynamic)
                                currentCosmetic.add(result.records[0].fields)
                            },

                            { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                        )
                }
            }
        }else {
            val tvdynamic = TextView(this)
            tvdynamic.textSize = 20f
            tvdynamic.text = "select another photo" //replace this with value
            linearLayout.addView(tvdynamic)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Adds functionaries to menu buttons
        return when (item.itemId) {
            R.id.saveButton -> {
//                // TODO: add saveing to database
                showAddItemDialog(this)

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        disposable2?.dispose()
    }

    private fun saveCosmetic(name: String, cosmetic: List<Fields>){       // sprawdzenie czy składniki są już w bazie i wywołanie zapisu

        val ingredientList = mutableListOf<Int>()
        val iL= arrayListOf<Ingredient_db>()

        val serviceIng = Retrofit.Builder()
            .baseUrl(baseURL) //base address of REST api for db
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseIngredientService::class.java)

        val servicePostIng = Retrofit.Builder()
            .baseUrl(baseURL) //base address of REST api for db
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabasePOSTIngredientService::class.java)
        
        val ingredientObservables = mutableListOf<Observable<Ingredient_db>>()
        val disposable2 = serviceIng.getIngredient()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    { result ->
                        for (ingredient in cosmetic){

                            val id = result.find{it.name==ingredient.inci_name}?.id
                            println(id.toString())
                            if (id != null){
                                ingredientList.add(id)
                                println(id.toString())
                                println(ingredientList.toString())
                                println("hoho")
                            }else{
                                println("helloooo")
                                var idNew = 0
                                if (result.isNotEmpty()){
                                    idNew = result.last().id+1
                                }
                                println(idNew.toString())
                                val ingredientDb = Ingredient_db(idNew, ingredient.inci_name, ingredient.function, false)
                                ingredientObservables.add(servicePostIng.postIngredient(ingredientDb).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                                ingredientList.add(idNew)
                           }
                        }
                        if (ingredientObservables.isNotEmpty()){
                            Observable.zip(ingredientObservables) {
                                list -> list.asList()
                            } .subscribeOn(Schedulers.io())
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribe({
                                  @Suppress("UNCHECKED_CAST") val result = it as List<Ingredient_db>
                                  println("xd")
                                  println(ingredientList.toString())
                                  cosmeticPost(name, ingredientList)
                              },{
                                  err ->  Toast.makeText(applicationContext, err.message, Toast.LENGTH_SHORT).show()
                              })
                        }else{
                              cosmeticPost(name, ingredientList)
                        }
                    },
                    {error ->   Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    })
    }
    private fun cosmeticPost( name:String, ingredientList: List<Int>){       // zapis kosmetyku do bazy danych
        val serviceCosm = Retrofit.Builder()
            .baseUrl(baseURL) //base address of REST api for db
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabasePOSTService::class.java)
             val ingrediens = ingredientList.toIntArray().toTypedArray()
             val cosmeticDb = Cosmetic_save(name, false, ingrediens)
              serviceCosm.postCosmetics(cosmeticDb)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(
                      {
                        Toast.makeText(applicationContext,"saved successfully",Toast.LENGTH_SHORT).show()
                      },
                      {
                          error -> Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
                      }
                  )
    }

    private fun showAddItemDialog(c: Context) {     // funkcja ta wyświetlaokno z zapytaniem onazwę kosmetyku i inicjalizuje zapis dobazy danych
        val taskEditText = EditText(c)
        val dialog = AlertDialog.Builder(c)
            .setTitle("Cosmetic name")
            .setMessage("Insert name of the cosmetic to save")
            .setView(taskEditText)
            .setPositiveButton("Save") { dialog, which ->
                    val currentName = taskEditText.text.toString()
                    saveCosmetic(currentName, currentCosmetic)
                }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}