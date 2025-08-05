package com.example.winksycall.utility

import com.google.firebase.database.FirebaseDatabase

object FirebaseUtils {
    fun getUidFromEmail(email: String, onResult: (String?) -> Unit) {
        val safeEmail = email.replace(".", "_").replace("@", "_")
        val ref = FirebaseDatabase.getInstance().getReference("emailToUid/$safeEmail")
        ref.get().addOnSuccessListener {
            val uid = it.getValue(String::class.java)
            onResult(uid)
        }.addOnFailureListener {
            onResult(null)
        }
    }
}
