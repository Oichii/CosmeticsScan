package com.AD.cosmeticsscan

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.Intent.ACTION_PICK
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var count = 0
    val PICK_IMAGE_CODE = 666
    val PERMISSION_CODE = 333
    var ingreedients_list = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ingredient.setMovementMethod(ScrollingMovementMethod())
        ingredient.text = "It will be supper cute app"
        //selText.text = "select photo from gallery:"
    }

    fun countB(view: View) {
        count++
        ingredient.text = "hello $count"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check for the permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // ask for the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
                ingredient.text = "hello"
            } else {
                pickImageFromGallery()
            }
        } else {
            pickImageFromGallery()
        }
        // recognise text on image
        //
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "please let me see your photos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        //val intent = Intent(ACTION_PICK)
        val intent = Intent(ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "sellect image"), PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE)
            img.setImageURI(data?.data)

        val image: FirebaseVisionImage
        val uri = data?.data.toString()
        val urii = uri.toUri()
        image = FirebaseVisionImage.fromFilePath(applicationContext, urii)

        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                ingreedients_list = firebaseVisionText.text
                ingredient.text = firebaseVisionText.text

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "nie udalo sie XDD", Toast.LENGTH_SHORT).show()
            }
    }


   fun analyze(view: View){
       val intent = Intent(this, IngreedientsActivity:: class.java)

       if (ingreedients_list != null){
           intent.putExtra("ingredients_list", ingreedients_list)
       }else{
           ingredient.text = "hallo"
       }
       startActivity(intent)
   }
}
