package com.example.cityguest.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Copia un file immagine generico (identificato tramite un [Uri]) all'interno
 * della memoria interna privata dell'applicazione.
 *
 * Questo metodo assicura che le immagini caricate dall'utente
 * vengano persistite in modo sicuro in `context.filesDir`. In questo modo le risorse rimangono
 * accessibili all'applicazione anche in seguito a chiusure o riavvii del dispositivo.
 *
 * @param context Il contesto dell'applicazione, necessario per utilizzare il `ContentResolver` e accedere alla directory dei file locale.
 * @param uri L'indirizzo URI del file di origine.
 * @param fileName Il nome con il quale il file verrà salvato all'interno dello storage interno.
 * @return Il percorso assoluto ([String]) del file salvato localmente, oppure `null` qualora la copia dovesse fallire.
 */
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