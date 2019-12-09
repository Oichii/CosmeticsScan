package com.AD.cosmeticsscan

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
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
    private var disposable_ing: Disposable? = null
    private val baseURL = "http://192.168.0.17:8000"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cosmetic_list)
        setSupportActionBar(toolbar)

        val service_cosm = Retrofit.Builder()
            .baseUrl(baseURL) //base address of REST api for db
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DatabaseService::class.java)

        val cosmeticsLayout = findViewById<LinearLayout>(R.id.ListofCosmetics)
        disposable = service_cosm.getCosmetics()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    for (cosm in result){
                        val tvdynamic = TextView(this)
                        tvdynamic.textSize = 25f
                        tvdynamic.text = getString(R.string.cosmetic_description, cosm.name)
                        cosmeticsLayout.addView(tvdynamic)
                    }
                    },
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
            )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
