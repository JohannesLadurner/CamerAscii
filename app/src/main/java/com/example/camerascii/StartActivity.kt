package com.example.camerascii

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

val SELECT_PICTURE = 200

class StartActivity : AppCompatActivity() {


    override fun onStart() {
        super.onStart()
        //Check if user has to give permission to access the camera, if not ask him!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA)
            val permissions: MutableList<String> = ArrayList()
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA)
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toTypedArray(), 111)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //FROMCAMERA button, switch to camera
        findViewById<Button>(R.id.fromCamera).setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        //FROMGALLERY button, switch to gallery, select picture and send to MainActivity
        findViewById<Button>(R.id.fromGallery).setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(i, SELECT_PICTURE);

        }
    }

    /*
    Calls, when you select a picture from the gallery
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            val intent = Intent(this, GalleryToAsciiActivity::class.java)
            intent.putExtra("Picture", imageUri)
            startActivity(intent)

        }
    }
}
