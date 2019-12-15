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
import kotlinx.android.synthetic.main.activity_cosmetic_list.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CosmeticListActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    private val baseURL =  "http://192.168.0.17:8000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cosmetic_list)
        setSupportActionBar(cosmeticName)

        val serviceCosm = Retrofit.Builder()
            .baseUrl(baseURL) //base address of REST api for db
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseService::class.java)

        val cosmetics = mutableListOf<TextView>()

        val cosmeticsLayout = findViewById<LinearLayout>(R.id.ListofCosmetics)
        disposable = serviceCosm.getCosmetics()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    for (cosm in result){
                        val tvdynamic = TextView(this)
                        cosmetics.add(tvdynamic)
                        tvdynamic.textSize = 25f
                        tvdynamic.text = getString(R.string.cosmetic_description, cosm.name)
                        cosmeticsLayout.addView(tvdynamic)
                        tvdynamic.setOnClickListener{
                            openCosmetic(cosm.name, cosm.ingredients)
                        }
                    }
                    },
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
            )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

    private fun openCosmetic(name:String, ing_list: Array<Int>){ //opens activity with ingredients of saved cosmetic
        val intent = Intent(this, CosmeticActivity:: class.java)

        val extras = Bundle()
        extras.putString("cosmetic_name", name)
        extras.putIntArray("ingredients_list", ing_list.toIntArray())
        intent.putExtras(extras)
        startActivity(intent)
    }

}
