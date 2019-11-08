package com.AD.cosmeticsscan

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_ingreedients.*
import kotlinx.android.synthetic.main.activity_scrolling.*

class ScrollingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val linearLayout = findViewById<LinearLayout>(R.id.linear)
        val extras = intent.extras
        if (extras != null) {
            val ingredientsList = extras.getString("ingredients_list")

            if (ingredientsList!= null) {
                val elements = ingredientsList.split(",")
                var first = elements[0].toString().split(" ")
                for(element in elements){
                    val tvdynamic = TextView(this)
                    tvdynamic.textSize = 20f
                    tvdynamic.text = element
                    linearLayout.addView(tvdynamic)

                }
            }
        }else {
            ingredients_list.text = "ingredients list is empty select another photo"
        }
    }
}
