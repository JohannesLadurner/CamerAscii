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

        /**
         *
            This method combines all the other methods, only this method can be called:
            1. Get all Pixels from the image
            2. Fit the size of the picture so 2x3 fields can be created
            3. Transform the pixels into binary values (depends on brigthness, 0 = dark, 1 = brigth)
            4. Create 3x2 fields from the binary image
            6. Turn fields into chars
        */

        fun getAsciiImage(bitmap: Bitmap): Array<String?> {
            var pixels = getBitmapPixels(bitmap)
            var asciiImage = Array<String?>(bitmap.height) { null }

            var pixelGroup = ""
            var row = 0
            var asciiRowCounter = 0
            while(row < newBitmapHeight * newBitmapWidth){
                var col = 0
                var asciiRow = ""
                while(col < newBitmapWidth) {
                    pixelGroup = ""
                    //Upper left, Upper right
                    pixelGroup += getBinaryPixel(pixels[row + col]).toString()
                    pixelGroup += getBinaryPixel(pixels[row + col + 1]).toString()

                    //Middle right, Middle left
                    pixelGroup += getBinaryPixel(pixels[row + col + newBitmapWidth]).toString()
                    pixelGroup += getBinaryPixel(pixels[row + col + newBitmapWidth + 1]).toString()

                    //Bottom left, Bottom right
                    pixelGroup += getBinaryPixel(pixels[row + col + newBitmapWidth * 2]).toString()
                    pixelGroup += getBinaryPixel(pixels[row + col + newBitmapWidth * 2 + 1]).toString()

                    //Safe to result row
                    asciiRow += getCharacterFromGroup(pixelGroup)

                    col += 2
                }

                //Safe row to result data
                asciiImage[asciiRowCounter] = asciiRow
                asciiRowCounter++

                //Move 3 rows down
                row += newBitmapWidth*3
            }
            return asciiImage
        }

        /**
         * This method converts the given bit-string to a single character, for example:
         * 0 0            1 1            0 0
         * 1 1 -> '-'  ,  1 1 -> 'P'  ,  0 0 -> '_'   ect.
         * 0 0            1 0            1 1
         *
         * The given string gets split up like this:   01  |  11  | 00
         *                                          upper  middle  bottom
         */
        private fun getCharacterFromGroup(group:String):Char{
            return '#'
        }

        /**
         * This method converts the colors of a pixel to a brightness range:
         *  0 = black, 255 = white
         *  Since we can only use binary values, everything above 50% is considered white (1), the rest is black (0).
         *
         */
        private fun getBinaryPixel(pixel: Int): Int {

            var brightness =
                (0.2126 * Color.red(pixel) + 0.7152 * Color.green(pixel) + 0.0722 * Color.blue(pixel))
            if (brightness >= 255.0/2.0) {
                return 1
            }
            return 0
        }
    }
}