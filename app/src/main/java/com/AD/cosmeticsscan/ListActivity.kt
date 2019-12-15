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
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import java.util.*

class ListActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    private var disposable2: Disposable? = null
    private var disposable3: Disposable? = null
    private val baseURL = "http://192.168.0.17:8000"
    private var currentCosmetic = mutableListOf<Fields>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(cosmeticName)

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
                val ingredientTextView = mutableListOf<TextView>()
                for ((number, _) in elements.withIndex()){
                    ingredientTextView.add(number, TextView(this))
                    ingredientTextView[number].textSize = 20f
                    linearLayout.addView(ingredientTextView[number])
                }

                for((number, element)in elements.withIndex()){
                    // TODO: sort ingredients in previous order
                    // TODO: check if restriction is present and if is print it as well
                    // TODO: add buttons to set ingredient as favourite

                    disposable = service.checkIngredient("cosmetic-ingredient-database-ingredients-and-fragrance-inventory",element, 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { result ->

                                if (result.nhits==0){
                                    ingredientTextView[number].text = getString(R.string.ingredient_not_found, (number+1).toString(), element.toLowerCase(Locale.getDefault()))
                                }else{
                                    ingredientTextView[number].text = getString(R.string.ingredient_description, (number+1).toString(), element.toLowerCase(Locale.getDefault()), result.records[0].fields.function.toLowerCase(Locale.getDefault()))
                                    currentCosmetic.add(result.records[0].fields)
                                }

                            },

                            { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                        )
                }
            }
        }else {
            val tvdynamic = TextView(this)
            tvdynamic.textSize = 20f
            tvdynamic.text = getString(R.string.select_photo)
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
        disposable3?.dispose()
    }

    private fun saveCosmetic(name: String, cosmetic: List<Fields>){       // sprawdzenie czy składniki są już w bazie i wywołanie zapisu

        val ingredientList = mutableListOf<Int>()


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
                            if (id != null){
                                ingredientList.add(id)
                            }else{
                                var idNew = 0
                                if (result.isNotEmpty()){
                                    idNew = result.last().id+1
                                }
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
              disposable3 = serviceCosm.postCosmetics(cosmeticDb)
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
            .setTitle("Save as Favourite")
            .setMessage("Insert name of the cosmetic to save")
            .setView(taskEditText)
            .setPositiveButton("Save") { _, _ ->
                    val currentName = taskEditText.text.toString()
                    saveCosmetic(currentName, currentCosmetic)
                }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }
}