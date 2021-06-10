package com.example.camerascii

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.*
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream


class CameraActivity : Activity(), PictureCallback, Camera.PreviewCallback, SurfaceHolder.Callback {

    private val DEBUG_TAG: String? = "MakePhotoActivity"
    private var camera: Camera? = null
    private var sampleSize = 8
    private var brightness = 75
    private lateinit var camView: SurfaceView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        // do we have a camera?
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show()
        }
        camView = findViewById<SurfaceView>(R.id.surfaceViewCamera)
        var holder = camView.holder
        holder.addCallback(this)

        /*
        change sampleSize of the Image in the upper seekbar
         */
        val textSize = findViewById<TextView>(R.id.textSampleCamera)
        findViewById<SeekBar>(R.id.sampleSizeCamera).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                when (i) {
                    0 -> sampleSize = 4
                    1 -> sampleSize = 8
                    2 -> sampleSize = 16
                    3 -> sampleSize = 32
                    4 -> sampleSize = 64
                }
                textSize.text = "Sample Size = $sampleSize"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        /*
       change brightness of the Image in the lower seekbar
        */
        val brightText = findViewById<TextView>(R.id.textBrightnessCamera)
        findViewById<SeekBar>(R.id.brightnessCamera).setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar

                brightText.text = "Brightness = $i"
                brightness = i
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
        var flashIsOn : Boolean = false
        findViewById<Button>(R.id.buttonCamera).setOnClickListener(){
            var p : Camera.Parameters? = camera?.parameters
            if(p != null){
                if(!flashIsOn){
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                }else{
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                }
                camera?.parameters = p
            }
            flashIsOn = !flashIsOn
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        camera?.startPreview()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        camera?.stopPreview()
        //camera?.release()
        //camera=null
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        try {
            if (camera != null) {
                camera?.release()
                camera = null
            }
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
            camera?.setPreviewDisplay(camView.holder)
            camera?.setPreviewCallback(this)
            camera?.setDisplayOrientation(90) //Set to Vertical
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "failed to open Camera")
            e.printStackTrace()
        }
    }

    override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
        Log.i(DEBUG_TAG, "Photo taken")
        camera?.release()
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        var parameters = camera!!.parameters
        var width = parameters.previewSize.width
        var height = parameters.previewSize.height

        //Convert
        var yuv = YuvImage(data, parameters.previewFormat, width, height, null)
        var out = ByteArrayOutputStream()
        yuv.compressToJpeg(Rect(0, 0, width, height), 50, out)
        var bytes: ByteArray = out.toByteArray()

        var options = BitmapFactory.Options()
        options.inSampleSize =
            sampleSize //Quality of the bitmap, for example 4 means width/height is 1/4 of the original image, and 1/16 of the pixels. Has to be a power of 2.
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        // create a matrix for the manipulation
        val matrix = Matrix()
        // rotate the Bitmap 90 degrees (counterclockwise)
        matrix.postRotate(90F)

        // recreate the new Bitmap, swap width and height and apply transform
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        //Get Image
        var image = ImageToAscii.getAsciiImage(rotatedBitmap, brightness.toDouble(), true)
        printAsciiImageOnView(image)
    }

    fun printAsciiImageOnView(image: Array<String?>) {
        var textView = findViewById<EditText>(R.id.asciiImageCamera)


        var pixelPerChar: Float = textView.width.toFloat() / image[0]!!.length
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelPerChar * 1.75f)
        var newText = ""
        for (line in image) {
            newText += line + "\n"
        }
        textView.setHorizontallyScrolling(true) //Allow text to go outside the view field in a single line
        textView.setText(newText)
    }
}

