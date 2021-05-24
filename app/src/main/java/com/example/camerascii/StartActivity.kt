package com.example.camerascii

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

val SELECT_PICTURE = 200
class StartActivity : AppCompatActivity() {
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

        findViewById<FloatingActionButton>(R.id.infoButton).setOnClickListener {
            showDialoge()
        }
    }
    /*
    Calls, when you select a picture from the gallery
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = data?.data
                val intent = Intent(this,GalleryToAsciiActivity::class.java)
                intent.putExtra("Picture",imageUri)
                startActivity(intent)

        }
    }

    fun showDialoge(){
        val dia = Dialog(this)
        dia.setContentView(R.layout.about_dialoge)
        dia.show()

        dia.findViewById<FloatingActionButton>(R.id.mailFab).setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("lukasbu@edu.aau.at"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "CameraAscii")
            intent.putExtra(Intent.EXTRA_TEXT, "mail body")
            startActivity(Intent.createChooser(intent, ""))

        }
        dia.findViewById<FloatingActionButton>(R.id.gitFab).setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/JohannesLadurner/CamerAscii"))
            startActivity(browserIntent)
        }
    }
}
