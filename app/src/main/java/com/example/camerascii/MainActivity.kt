package com.example.camerascii

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import java.io.ByteArrayOutputStream


class MainActivity : Activity(), PictureCallback, Camera.PreviewCallback {

    private val DEBUG_TAG: String? = "MakePhotoActivity"
    private var camera: Camera? = null

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
        setContentView(R.layout.activity_main)

        // do we have a camera?
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show()
        }
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
            val view = findViewById<SurfaceView>(R.id.surfaceView)
            camera?.setPreviewDisplay(view.holder)

            //Also Possible instead of camera.setPreviewDisplay(holder) (Without surfaceView in xml):
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
        options.inSampleSize = 16
        var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

        //Draw on SurfaceView
        /*
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        var view = findViewById<SurfaceView>(R.id.surfaceView)
        view.draw(canvas)

         */
        var pixels = ImageToAscii.getBitmapPixels(bitmap)

    }


}
