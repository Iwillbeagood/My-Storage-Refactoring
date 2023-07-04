package com.example.mystorage.utils.image

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import java.net.MalformedURLException
import java.net.URL

object ImageUtils {
    fun isUrl(input: String): Boolean {
        return try {
            URL(input)
            true
        } catch (e: MalformedURLException) {
            false
        }
    }

    fun isBitmap(input: String): Boolean {
        return try {
            val byteArray = Base64.decode(input, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size) != null
        } catch (e: Exception) {
            false
        }
    }

    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(uri, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)

        return result!!
    }
}
