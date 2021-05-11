package com.example.camerascii

import android.graphics.Bitmap

class ImageToAscii {

    companion object {
        fun getBitmapData(bitmap: Bitmap): IntArray {
            var pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 1, 1, bitmap.width - 1, bitmap.height - 1)
            return pixels
        }
    }
}