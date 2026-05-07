package com.example.cityguest.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.cityguest.data.UserRepository

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var profileImageUri by mutableStateOf<Uri?>(null)
    var newPassword by mutableStateOf("")

    fun initUser(userEmail: String, userName: String) {
        email = userEmail
        username = userName
    }
}