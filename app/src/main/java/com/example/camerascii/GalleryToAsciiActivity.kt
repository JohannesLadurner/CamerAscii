package com.example.camerascii

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.EditText
import android.widget.ImageView

var selectedImage: Bitmap? = null
class GalleryToAsciiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_to_ascii)

        /*
        Get a Picture from gallery and convert to Bitmap
         */
        val intent = intent
        val imageUri = intent.extras?.getParcelable<Uri>("Picture")
        val imageStream = imageUri?.let { contentResolver.openInputStream(it) }
        var options = BitmapFactory.Options()
        options.inSampleSize = 16//Quality of the bitmap, for example 4 means width/height is 1/4 of the original image, and 1/16 of the pixels. Has to be a power of 2.
        selectedImage = BitmapFactory.decodeStream(imageStream,null,options)

        /*
        Preview of the original Image
         */
        findViewById<ImageView>(R.id.originalImage).setImageURI(imageUri)

        var image = ImageToAscii.getAsciiImage(selectedImage!!,75.0)
        printAsciiImageOnView(image)


    }
    fun printAsciiImageOnView(image:Array<String?>){
        var textView = findViewById<EditText>(R.id.asciiPrev)
        var newText = ""
        for(line in image){
            newText += line + "\n"
        }
        textView.setText(newText)
    }
}