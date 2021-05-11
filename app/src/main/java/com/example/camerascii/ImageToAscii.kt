package com.example.camerascii

import android.graphics.Bitmap
import android.graphics.Color

class ImageToAscii {


    companion object {
        var newBitmapWidth: Int = 0
        private var newBitmapHeight: Int = 0

        fun getBitmapPixels(bitmap: Bitmap): IntArray {
            var pixels = IntArray(bitmap.width * bitmap.height)

            //Since we build 3x2 blocks, the width has to be divisable by 2, and the height by 3. Cut the remaining columns/rows off!
            newBitmapHeight = bitmap.height - (bitmap.height % 3)
            newBitmapWidth = bitmap.width - (bitmap.width % 2)
            bitmap.getPixels(
                pixels, 0, bitmap.width, 0, 0,
                newBitmapWidth,
                newBitmapHeight
            )
            return pixels
        }

        fun getBinaryPixel(pixel: Int, threshold: Int): Int {

            var brightness =
                (0.2126 * Color.red(pixel) + 0.7152 * Color.green(pixel) + 0.0722 * Color.blue(pixel))
            if (brightness >= threshold) {
                return 1
            }
            return 0
        }
    }
}