package com.example.calypsodivelog.service

import android.content.Context
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import java.io.ByteArrayOutputStream


object ImageConverterService {

    fun resizeBitmap(mBitmap: Bitmap, newWidth: Float, newHeight: Float): Bitmap {
        val width = mBitmap.width
        val height = mBitmap.height
        val scaleWidth = newWidth / width
        val scaleHeight = newHeight / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false)
    }

    fun convertStringToBitmap(imagen: String?): Bitmap {
        val decodedString: ByteArray = Base64.decode(imagen, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun convertResourceToBitmap(recurso: Int, context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.getResources(), recurso)
    }

    fun convertBitmaptoString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val byte_arr: ByteArray = stream.toByteArray()
        return Base64.encodeToString(byte_arr, Base64.DEFAULT)
    }

    fun getBytesFromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
            return stream.toByteArray()
        }
        return null
    }

    fun getFromBitmapBytes(imagenbyte: ByteArray?): Bitmap? {
        return if (imagenbyte != null) {
            BitmapFactory.decodeByteArray(imagenbyte, 0, imagenbyte.size)
        } else null
    }
}
