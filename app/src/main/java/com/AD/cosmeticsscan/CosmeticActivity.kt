package com.AD.cosmeticsscan

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cosmetic.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

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

        //setting the name using extras
        val toolbar = findViewById<Toolbar>(R.id.cosmeticName)
        val extras = intent.extras
        if (extras != null){
            println("helooo")
            val name = extras.getString("cosmetic_name")
            supportActionBar?.title = name
        }

        val ingredientLayout = findViewById<LinearLayout>(R.id.ListofIngredients)
        disposable = serviceIng.getIngredient()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    for ((num, ingr) in result.withIndex()){
                        val tvdynamic = TextView(this)


                        tvdynamic.textSize = 25f
                        tvdynamic.text = getString(R.string.ingredient_description, (num+1).toString(),  ingr.name.toLowerCase(Locale.getDefault()), ingr.function.toLowerCase(Locale.getDefault()))
                        ingredientLayout.addView(tvdynamic)
                    }
                },
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
            )
    }
    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
