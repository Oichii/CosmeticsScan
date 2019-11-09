package com.AD.cosmeticsscan

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val linearLayout = findViewById<LinearLayout>(R.id.linear)
        val extras = intent.extras
        if (extras != null) {
            val ingredientsList = extras.getString("ingredients_list")

            if (ingredientsList!= null) {
                val ingredientList = ingredientsList.removePrefix("INGREDIENTS")
                val iList = ingredientList.replace("\\s".toRegex(), " ")
                val elements = iList.split(",")
                var first = elements[0].split(" ")

                for(element in elements){
                    val tvdynamic = TextView(this)
                    tvdynamic.textSize = 20f
                    tvdynamic.text = element
                    linearLayout.addView(tvdynamic)

                }
            }
        }else {
            val tvdynamic = TextView(this)
            tvdynamic.textSize = 20f
            tvdynamic.text = "select another photo"
            linearLayout.addView(tvdynamic)
        }
    }
}



