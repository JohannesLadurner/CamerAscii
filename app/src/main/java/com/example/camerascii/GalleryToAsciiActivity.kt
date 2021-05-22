package com.example.camerascii

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.SurfaceView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import java.io.InputStream

private var sampleSize = 8
private var brightness = 75


private var originalImage: Uri? = null
class GalleryToAsciiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_to_ascii)

        //Get the original Image
        val intent = intent
        originalImage = intent.extras?.getParcelable<Uri>("Picture")

        /*
        Preview of the original Image
         */
        findViewById<ImageView>(R.id.originalImage).setImageURI(originalImage)
        galleryToAscii()

        /*
       change sampleSize of the Image in the upper seekbar
        */
        val textSize = findViewById<TextView>(R.id.textSampleGallery)
        findViewById<SeekBar>(R.id.sampleSizeGallery).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                when(i){
                    0 -> sampleSize = 4
                    1 -> sampleSize = 8
                    2 -> sampleSize = 16
                    3 -> sampleSize = 32
                    4 -> sampleSize = 64
                }
                textSize.text = "Sample Size = $sampleSize"
                galleryToAscii()

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        /*
       change brightness of the Image in the lower seekbar
        */
        val brightText = findViewById<TextView>(R.id.textBrightnessGallery)
        findViewById<SeekBar>(R.id.brightnessGallery).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar

                brightText.text = "Brightness = $i"
                brightness = i
                galleryToAscii()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
    //Transform the Original Image to Ascii
    fun galleryToAscii(){
        var original = originalImage?.let { contentResolver.openInputStream(it) }
        var options = BitmapFactory.Options()
        options.inSampleSize = sampleSize //Quality of the bitmap, for example 4 means width/height is 1/4 of the original image, and 1/16 of the pixels. Has to be a power of 2.
        var selectedImage = BitmapFactory.decodeStream(original,null,options)
        var image = ImageToAscii.getAsciiImage(selectedImage!!, brightness.toDouble())
        printAsciiImageOnView(image)
    }

    fun printAsciiImageOnView(image:Array<String?>){
        var textView = findViewById<EditText>(R.id.asciiPrev)
        var pixelPerChar:Float = (textView.resources.displayMetrics.widthPixels.toFloat() / image[0]!!.length)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelPerChar*1.6f)
        var newText = ""
        for(line in image){
            newText += line + "\n"
        }
        textView.setText(newText)
    }
}