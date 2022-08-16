package com.example.toastlibrary

import android.Manifest
import android.R.attr
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class CameraActivity : AppCompatActivity() {
    lateinit var image: ImageView
    private val pic_id = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val btnPhoto = findViewById<Button>(R.id.btnPhoto)
        image = findViewById<ImageView>(R.id.image)
        btnPhoto.setOnClickListener {
            locationPermission()
        }


    }

    fun locationPermission() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
                ), 1
            )
            return;
        } else {
            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Start the activity with camera_intent,
            // and request pic id

            // Start the activity with camera_intent,
            // and request pic id
            startActivityForResult(camera_intent, pic_id)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                   locationPermission()
                } else {
                    locationPermission()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === pic_id) {
            image.setImageBitmap(data!!.getExtras()!!.get("data") as Bitmap?)
        }
    }

}