package com.AD.cosmeticsscan

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import java.net.URL

class ListActivity : AppCompatActivity() {
    val baseURL = "https://public.opendatasoft.com/api/records/1.0/search/?dataset=cosmetic-ingredient-database-ingredients-and-fragrance-inventory&q=zinc+oxide&lang=en&rows=1&facet=update_date&facet=restriction&facet=function"
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
    // TODO: make an api calls to the database and print the result
    private fun sendReguest(){
        val result = URL("<API Call>").readText()

    }
}



