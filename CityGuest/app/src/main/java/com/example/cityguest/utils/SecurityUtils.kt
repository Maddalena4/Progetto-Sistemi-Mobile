package com.example.cityguest.utils

import java.security.MessageDigest

//funzione hash per avere una password salvata come hash e non come stringa così è più sicura
fun hashPassword(password: String): String{
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)

    return digest.joinToString("") { "%02x".format(it) }
}
//funzione per avere un formato di email
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}