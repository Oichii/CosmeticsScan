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


class ListActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
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
                                linearLayout.addView(tvdynamic)},
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
                // TODO: add saveing to database
                Toast.makeText(this, "ingredient saved", Toast.LENGTH_SHORT).show()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}



