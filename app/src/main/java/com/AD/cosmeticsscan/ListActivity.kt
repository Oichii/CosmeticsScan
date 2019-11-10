package com.AD.cosmeticsscan

import android.os.Bundle

import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.content_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path



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
                val ingredientList = ingredientsList.removePrefix("INGREDIENTS")
                val iList = ingredientList.replace("\\s".toRegex(), " ")
                val elements = iList.split(",")

                for(element in elements){
// TODO: sort ingredients in previous order
                    disposable = service.checkIngredient("cosmetic-ingredient-database-ingredients-and-fragrance-inventory",element, 1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { result ->
                                val tvdynamic = TextView(this)
                                tvdynamic.textSize = 20f
                                tvdynamic.text = getString(R.string.ingredient_description, element, result.records[0].fields.function)
                                linearLayout.addView(tvdynamic)},
                            { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                        )
                }
            }
        }else {
            val tvdynamic = TextView(this)
            tvdynamic.textSize = 20f
            tvdynamic.text = "select another photo"
            linearLayout.addView(tvdynamic)
        }

    }
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
//https://public.opendatasoft.com/
///api/records/1.0/search/?dataset=cosmetic-ingredient-database-ingredients-and-fragrance-inventory&q=zinc+oxide&lang=en&rows=1


