package com.example.cityguest.utils

import java.security.MessageDigest

/**
 * Elabora l'hashing crittografico
 * Viene invocato durante la registrazione e il login per confrontare i digest delle password,
 * azzerando il rischio di memorizzare credenziali in chiaro all'interno del database SQLite.
 *
 * @param password La stringa alfanumerica inserita in chiaro dall'utente.
 * @return Stringa esadecimale a 64 caratteri rappresentante l'impronta digitale univoca (Hash) della password.
 */
fun hashPassword(password: String): String{
    val bytes = password.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)

    return digest.joinToString("") { "%02x".format(it) }
}
/**
 * Valida la correttezza formale di un indirizzo e-mail inserito nei form di input.
 *
 * @param email La stringa di testo inserita dall'utente nel campo email.
 * @return Vero se l'indirizzo soddisfa l'espressione regolare standard di Android, Falso in caso contrario.
 */
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}