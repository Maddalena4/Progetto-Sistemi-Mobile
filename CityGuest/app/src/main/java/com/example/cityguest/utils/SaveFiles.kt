package com.example.cityguest.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun saveImageToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            file.absolutePath
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}