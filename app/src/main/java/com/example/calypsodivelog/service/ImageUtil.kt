package com.example.calypsodivelog.service

import android.content.Context
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtil {

    // TODO(): Redimensionar la foto manteniendo la escala
    fun resizeImage(imgBitmap: Bitmap, newWidth: Float, newHeight: Float): Bitmap {
        val width = imgBitmap.width
        val height = imgBitmap.height
        val scaleWidth = newWidth / width
        val scaleHeight = newHeight / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(imgBitmap, 0, 0, width, height, matrix, false)
    }

    fun convertStringToBitmap(imgString: String?): Bitmap? {
        if(imgString?.isNotEmpty() == true) {
            val decodedString: ByteArray = Base64.decode(imgString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }else{
            return null
        }
    }

    fun convertResourceToBitmap(recurso: Int, context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.getResources(), recurso)
    }

    fun convertBitmapToString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val byte_arr: ByteArray = stream.toByteArray()
        return Base64.encodeToString(byte_arr, Base64.DEFAULT)
    }

    fun getByteArrayFromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
            return stream.toByteArray()
        }
        return null
    }

    fun getFromBitmapByteArray(imagenbyte: ByteArray?): Bitmap? {
        return if (imagenbyte != null) {
            BitmapFactory.decodeByteArray(imagenbyte, 0, imagenbyte.size)
        } else null
    }
}
