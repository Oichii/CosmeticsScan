package com.AD.cosmeticsscan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ingreedients.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_main.*


class IngreedientsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingreedients)
        ingredients.text = "Ingredients:"
        informations.setMovementMethod(ScrollingMovementMethod())
        ingredients_list.setMovementMethod(ScrollingMovementMethod())

        val extras = intent.extras
        if (extras != null) {
            val ingredientsList = extras.getString("ingredients_list")
            ingredients_list.text = ingredientsList

            if (ingredientsList!= null) {
                val elements = ingredientsList.split(",")
                informations.text = elements.toString()
                var first = elements[0].toString().split(" ")
                informations.text = first[0]

            }
        }else{
            ingredients_list.text = "ingredients list is empty select another photo"
        }

        //informations.text = "Informations about ingredients:"
    }
    fun send_question(){

    }

}
