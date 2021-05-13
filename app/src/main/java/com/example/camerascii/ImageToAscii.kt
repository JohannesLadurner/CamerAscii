package com.example.camerascii

import android.graphics.Bitmap
import android.graphics.Color

class ImageToAscii {

    companion object {
        private var newBitmapWidth: Int = 0
        private var newBitmapHeight: Int = 0

        /***
         * This method returns all the pixels from the given bitmap.
         * It makes sure that groups of 3x2 can be cutted out without remainder.
         */
        private fun getBitmapPixels(bitmap: Bitmap): IntArray {
            var pixels = IntArray(bitmap.width * bitmap.height)

            //Since we build 3x1 blocks, we have to make sure that the height is divisable by 3
            newBitmapHeight = bitmap.height - (bitmap.height % 3)
            newBitmapWidth = bitmap.width
            bitmap.getPixels(
                pixels, 0, bitmap.width, 0, 0,
                newBitmapWidth,
                newBitmapHeight
            )
            return pixels
        }

        /**
         *
            This method combines all the other methods, only this method can be called:
            1. Get all Pixels from the image
            2. Fit the size of the picture so 2x3 fields can be created
            3. Transform the pixels into binary values (depends on brigthness, 0 = dark, 1 = brigth)
            4. Create 3x2 fields from the binary image
            6. Turn fields into chars
        */

        fun getAsciiImage(bitmap: Bitmap, pixelThreshold:Double): Array<String?> {
            var pixels = getBitmapPixels(bitmap)
            var asciiImage = Array<String?>(bitmap.height / 3) { null }

            var pixelGroup = ""
            var row = 0
            var asciiRowCounter = 0
            while(row < newBitmapHeight){
                var col = 0
                var asciiRow = ""
                while(col < newBitmapWidth) {
                    pixelGroup = ""

                    //Top
                    pixelGroup += getBinaryPixel(pixels[row*newBitmapWidth + col],pixelThreshold)

                    //Middle
                    pixelGroup += getBinaryPixel(pixels[(row+1)*newBitmapWidth + col],pixelThreshold)

                    //Bottom
                    pixelGroup += getBinaryPixel(pixels[(row+2)*newBitmapWidth + col],pixelThreshold)

                    //Safe to result row
                    asciiRow += getCharacterFrom3x1Group(pixelGroup)

                    //Move one column to the right
                    col += 1
                }

                //Safe row to result data
                asciiImage[asciiRowCounter] = asciiRow
                asciiRowCounter++

                //Move 3 rows down
                row += 3
            }
            return asciiImage
        }

        private fun getCharacterFrom3x1Group(group:String):Char{
            when (group) {
                "000" -> return ' '
                "100" -> return '\''
                "010" -> return '-'
                "001" -> return '_'
                "110" -> return 'P'
                "101" -> return '1'
                "011" -> return '='
                "111" -> return '#'
            }
            return '#'
        }

        private fun getCharacterFrom2x1Group(group:String):Char{
            when (group) {
                "00" -> return ' '
                "10" -> return '^'
                "01" -> return '_'
                "11" -> return '#'
            }
            return '#'
        }

        /**
         * This method converts the colors of a pixel to a brightness range:
         *  0 = black (pixel), 255 = white (no pixel)
         *  Since we can only use binary values, everything above 50% is considered white (0), the rest is black (1).
         *
         */
        private fun getBinaryPixel(pixel: Int, threshold:Double): Int {

            var brightness =
                (0.2126 * Color.red(pixel) + 0.7152 * Color.green(pixel) + 0.0722 * Color.blue(pixel))
            if (brightness >= threshold) {
                return 0
            }
            return 1
        }
    }
}