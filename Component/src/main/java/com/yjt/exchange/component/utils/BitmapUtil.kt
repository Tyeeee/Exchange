package com.hynet.heebit.components.utils

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.io.IOException

class BitmapUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            BitmapUtil()
        }

    }

    @Throws(IOException::class)
    fun compress(original: Bitmap, width: Int, height: Int): ByteArray {
        val byteArrayOutputStream1 = ByteArrayOutputStream()
        original.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream1)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(byteArrayOutputStream1.toByteArray(), 0, byteArrayOutputStream1.toByteArray().size, options)
        options.inSampleSize = computeSampleSize(options, -1, width / 2 * height / 2)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeByteArray(byteArrayOutputStream1.toByteArray(), 0, byteArrayOutputStream1.toByteArray().size, options)
        LogUtil.instance.print(String.format("width:%s,height:%s,outWidth:%s,outHeight:%s,inSampleSize:%s,size:%sKB(%s),bitmapWidth:%s,bitmapHeight:%s", width, height, options.outWidth, options.outHeight, options.inSampleSize, bitmap.allocationByteCount / 1024, bitmap.allocationByteCount, bitmap.width, bitmap.height))
        val byteArrayOutputStream2 = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream2)
        byteArrayOutputStream1.flush()
        byteArrayOutputStream1.close()
        byteArrayOutputStream2.flush()
        byteArrayOutputStream2.close()
        return byteArrayOutputStream2.toByteArray()
    }

    private fun computeSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
        val lowerBound = if (maxNumOfPixels == -1) 1 else Math.ceil(Math.sqrt((options.outWidth * options.outHeight / maxNumOfPixels).toDouble())).toInt()
        val upperBound = if (minSideLength == -1) 128 else Math.min(Math.floor((options.outWidth / minSideLength).toDouble()), Math.floor((options.outHeight / minSideLength).toDouble())).toInt()
        if (upperBound < lowerBound) {
            return lowerBound
        }
        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
    }

    private fun computeInitialSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
        val lowerBound = if (maxNumOfPixels == -1) 1 else Math.ceil(Math.sqrt((options.outWidth * options.outHeight / maxNumOfPixels).toDouble())).toInt()
        val upperBound = if (minSideLength == -1) 128 else Math.min(Math.floor((options.outWidth / minSideLength).toDouble()), Math.floor((options.outHeight / minSideLength).toDouble())).toInt()
        if (upperBound < lowerBound) {
            return lowerBound
        }
        return if (maxNumOfPixels == -1 && minSideLength == -1) {
            1
        } else if (minSideLength == -1) {
            lowerBound
        } else {
            upperBound
        }
    }

    fun zoomDrawable(drawable: Drawable, w: Int, h: Int): Drawable {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val oldbmp = drawableToBitmap(drawable)
        val matrix = Matrix()
        val scaleWidth = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        matrix.postScale(scaleWidth, scaleHeight)
        val newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true)
        return BitmapDrawable(null, newbmp)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val config = if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return bitmap
    }
    
}