package com.example.cityguest.viewmodel

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityguest.data.UserRepository
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.cityguest.utils.hashPassword

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var profileImageUri by mutableStateOf<Uri?>(null)
    var newPassword by mutableStateOf("")

    fun initUser(userEmail: String, userName: String) {
        email = userEmail
        username = userName
        profileImageUri = null
        viewModelScope.launch {
            val user = repository.getUser(userEmail)
            user?.profileImageUri?.let {
                profileImageUri = it.toUri()
            }
        }
    }

    fun saveProfileChanges(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val currentUser = repository.getUser(email)
            if (currentUser != null) {

                val currentUser = repository.getUser(email)
                if(currentUser!= null){
                    val passwordToSave = if(newPassword.isNotEmpty()){
                        hashPassword(newPassword)
                    }else{
                        currentUser.password
                    }
                    val updateUser = currentUser.copy(username = username, password = passwordToSave, profileImageUri = profileImageUri?.toString())
                    repository.updateUser(updateUser)
                    newPassword = ""
                    onSuccess(username)
                }
            }
        }
    }
}