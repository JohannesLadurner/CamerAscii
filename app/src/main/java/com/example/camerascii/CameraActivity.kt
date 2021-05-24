package com.example.camerascii

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.SurfaceView
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayOutputStream


class CameraActivity : Activity(), PictureCallback, Camera.PreviewCallback {

    private val DEBUG_TAG: String? = "MakePhotoActivity"
    private var camera: Camera? = null
    private var sampleSize = 8
    private var brightness = 75
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
        setContentView(R.layout.activity_camera)

        // do we have a camera?
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show()
        }

        /*
        change sampleSize of the Image in the upper seekbar
         */
        val textSize = findViewById<TextView>(R.id.textSampleCamera)
        findViewById<SeekBar>(R.id.sampleSizeCamera).setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {

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
        findViewById<SeekBar>(R.id.brightnessCamera).setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener {

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
    }

    fun onClick(view: View?) {
        initCam()
        //camera?.takePicture(null, null, this)
    }

    fun initCam(){
        try {
            if(camera != null){
                camera?.release()
                camera = null
            }
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
            val view = findViewById<SurfaceView>(R.id.surfaceViewCamera)
            camera?.setPreviewDisplay(view.holder)

            //Also Possible instead of camera.setPreviewDisplay(holder) (Without surfaceView in xml): (PREVIEW DOES NOT UPDATE!)
            //val tex = SurfaceTexture(1)
            //camera?.setPreviewTexture(tex)

            camera?.setPreviewCallback(this)
            camera?.setDisplayOrientation(90) //Set to Vertical
            camera?.startPreview()
        } catch (e: Exception) {
            Log.e(DEBUG_TAG, "failed to open Camera")
            e.printStackTrace()
        }
    }

    override fun onPause() {
        if (camera != null) {
            camera?.release()
            camera = null
        }
        super.onPause()
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
        options.inSampleSize = sampleSize //Quality of the bitmap, for example 4 means width/height is 1/4 of the original image, and 1/16 of the pixels. Has to be a power of 2.
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        // create a matrix for the manipulation
        val matrix = Matrix()
        // rotate the Bitmap 90 degrees (counterclockwise)
        matrix.postRotate(90F)

        // recreate the new Bitmap, swap width and height and apply transform
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        //Get Image
        var image = ImageToAscii.getAsciiImage(rotatedBitmap,brightness.toDouble())
        printAsciiImageOnView(image)
    }

    fun  printAsciiImageOnView(image:Array<String?>){
        var textView = findViewById<EditText>(R.id.asciiImageCamera)



        var pixelPerChar:Float = textView.width.toFloat() / image[0]!!.length
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, pixelPerChar*1.75f)
        var newText = ""
        for(line in image){
            newText += line + "\n"
        }
        textView.setHorizontallyScrolling(true) //Allow text to go outside the view field in a single line
        textView.setText(newText)
    }


}
