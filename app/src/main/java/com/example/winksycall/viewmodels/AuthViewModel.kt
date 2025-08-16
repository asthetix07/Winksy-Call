package com.example.winksycall.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.winksycall.utility.FirebaseUtils
import com.example.winksycall.utility.saveCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    saveCredentials(context, email, password)
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.message)
                }
            }
    }

    fun signup(
        email: String,
        password: String,
        rememberMe: Boolean,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    if (rememberMe) {
                        saveCredentials(context, email, password)
                    }

                    // Save email -> UID mapping
                    val safeEmail = FirebaseUtils.encodeEmail(email)
                    val db = FirebaseDatabase.getInstance().reference
                    db.child("emailToUid").child(safeEmail).setValue(uid)

                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

}
