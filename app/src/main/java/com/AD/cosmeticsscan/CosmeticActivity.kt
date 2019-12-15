package com.AD.cosmeticsscan

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cosmetic.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class CosmeticActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    private val baseURL =  "http://192.168.0.17:8000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cosmetic)
        setSupportActionBar(cosmeticName)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val serviceIng = Retrofit.Builder()
            .baseUrl(baseURL) //base address of REST api for db
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseIngredientService::class.java)

        val extras = intent.extras
        if (extras != null){
            val name = extras.getString("cosmetic_name")
            supportActionBar?.title = name

            val ing_list = extras.getIntArray("ingredients_list")

            val ingredientTextView = mutableListOf<TextView>()
            val ingredientLayout = findViewById<LinearLayout>(R.id.ListofIngredients)
            for ((ing, _ )in ing_list!!.iterator().withIndex()){
                    ingredientTextView.add(ing, TextView(this))
                    ingredientTextView[ing].textSize = 20f
                    ingredientLayout.addView(ingredientTextView[ing])
            }

            disposable = serviceIng.getIngredient()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        for((num,ingr) in ing_list.withIndex()){
                            val ingredient = result.find { it.id == ingr }
                            ingredientTextView[num].text = getString(R.string.ingredient_description, (num+1).toString(), ingredient!!.name, ingredient!!.function)
                        }

                    },
                    { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )

        }

    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
