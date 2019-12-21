package com.AD.cosmeticsscan

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import android.widget.RelativeLayout.ALIGN_PARENT_RIGHT
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cosmetic_list.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.RelativeLayout
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.RelativeLayout.ALIGN_PARENT_LEFT
import kotlinx.android.synthetic.main.activity_main.view.*
import com.google.android.gms.tasks.Task
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback


class CosmeticListActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    private var disposable2: Disposable? = null
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



        disposable = serviceCosm.getCosmetics()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    for (cosm in result){
                        addRow(cosm)
                    }
                    },
                { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
            )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        disposable2?.dispose()
    }

    private fun openCosmetic(name:String, ing_list: Array<Int>){ //opens activity with ingredients of saved cosmetic
        val intent = Intent(this, CosmeticActivity:: class.java)

        val extras = Bundle()
        extras.putString("cosmetic_name", name)
        extras.putIntArray("ingredients_list", ing_list.toIntArray())
        intent.putExtras(extras)
        startActivity(intent)
    }
    private fun addRow(cosm: Cosmetic_db){
        val cosmetics = mutableListOf<TextView>()

        val cosmeticsLayout = findViewById<LinearLayout>(R.id.ListofCosmetics)

        val tvdynamic = TextView(this)
        val delButton = ImageButton(this)
        val vertical = LinearLayout(this)
        vertical.setVerticalGravity(LinearLayout.VERTICAL)

        delButton.setImageResource(R.drawable.delete)
        delButton.setBackgroundColor(getColor(R.color.colorBackground))
        delButton.adjustViewBounds=true

        delButton.layoutParams = LinearLayout.LayoutParams(
            120,
            120)



        cosmetics.add(tvdynamic)
        tvdynamic.textSize = 25f
        tvdynamic.text = getString(R.string.cosmetic_description, cosm.name)
        cosmeticsLayout.addView(vertical)
        vertical.addView(tvdynamic)
        vertical.addView(delButton)
        tvdynamic.setOnClickListener{
            openCosmetic(cosm.name, cosm.ingredients)
        }

        delButton.setOnClickListener {
            deleteCosmetic(cosm.id)
            vertical.removeAllViews()
            cosmeticsLayout.removeView(vertical)
        }

    }

    private fun deleteCosmetic(id:Int){
        val serviceCosm = Retrofit.Builder()
            .baseUrl(baseURL) //base address of REST api for db
            .build()
            .create(DatabaseService::class.java)

        serviceCosm.delCosmetic(id).enqueue(object : Callback<Void> {
            override fun onResponse( call: Call<Void>? , response: retrofit2.Response<Void>?) {
                Toast.makeText(applicationContext, "deleted successfully" , Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                Toast.makeText(applicationContext, t!!.message , Toast.LENGTH_SHORT).show()

            }
        }
        )

    }

}
