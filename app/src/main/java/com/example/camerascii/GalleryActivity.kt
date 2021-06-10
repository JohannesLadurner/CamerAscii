package com.example.camerascii

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.settings_gallery_dialoge.*

private var sampleSize = 8
private var brightness = 75


private var originalImage: Uri? = null
class GalleryToAsciiActivity : AppCompatActivity() {
    var drawDarkPixels = true
    var imgRotation = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        //Get the original Image
        val intent = intent
        originalImage = intent.extras?.getParcelable<Uri>("Picture")

        /*
        Preview of the original Image
         */
        findViewById<ImageView>(R.id.originalImageGallery).setImageURI(originalImage)
        galleryToAscii()

        /*
       change sampleSize of the Image in the upper seekbar
        */
        val textSize = findViewById<TextView>(R.id.textSampleGallery)
        findViewById<SeekBar>(R.id.sampleSizeGallery).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                when(i){
                    0 -> sampleSize = 2
                    1 -> sampleSize = 4
                    2 -> sampleSize = 8
                    3 -> sampleSize = 16
                    4 -> sampleSize = 32
                }
                textSize.text = "Quality = $sampleSize"
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

        findViewById<FloatingActionButton>(R.id.settingsGalleryButton).setOnClickListener(){
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.settings_gallery_dialoge)
            dialog.findViewById<Switch>(R.id.switchBlackWhiteGallery).isChecked = drawDarkPixels

            dialog.findViewById<FloatingActionButton>(R.id.rotateImage).setOnClickListener(){
                imgRotation = imgRotation - 90
                if(imgRotation == 0)
                    imgRotation = 360
                galleryToAscii()
            }
            dialog.findViewById<Switch>(R.id.switchBlackWhiteGallery).setOnCheckedChangeListener { compoundButton, isActive ->
                drawDarkPixels = isActive
                galleryToAscii()
            }
            dialog.show()
        }

    }
    //Transform the Original Image to Ascii
    fun galleryToAscii(){
        var original = originalImage?.let { contentResolver.openInputStream(it) }
        var options = BitmapFactory.Options()
        options.inSampleSize = sampleSize //Quality of the bitmap, for example 4 means width/height is 1/4 of the original image, and 1/16 of the pixels. Has to be a power of 2.
        var selectedImage = BitmapFactory.decodeStream(original,null,options)
        // create a matrix for the manipulation
        val matrix = Matrix()
        // rotate the Bitmap 90 degrees (counterclockwise)
        matrix.postRotate(imgRotation.toFloat())

        // recreate the new Bitmap, swap width and height and apply transform
        val rotatedBitmap =
            Bitmap.createBitmap(selectedImage!!, 0, 0, selectedImage.width, selectedImage.height, matrix, true)

        var image = ImageToAscii.getAsciiImage(rotatedBitmap, brightness.toDouble(), drawDarkPixels)
        printAsciiImageOnView(image)
    }

    fun printAsciiImageOnView(image:Array<String?>){
        var textView = findViewById<EditText>(R.id.asciiImageGallery)
        var pixelPerChar:Float = (textView.resources.displayMetrics.widthPixels.toFloat() / image[0]!!.length)

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelPerChar*1.6f)
        var newText = ""
        for(line in image){
            newText += line + "\n"
        }
        textView.setText(newText)
    }
}