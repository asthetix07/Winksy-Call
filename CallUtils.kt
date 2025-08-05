package com.example.winksycall.utility


import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

fun startVideoCall(context: Context, calleeEmail: String) {
    val currentUser = FirebaseAuth.getInstance().currentUser ?: return
    val currentUid = currentUser.uid

    val db = FirebaseDatabase.getInstance().reference

    db.child("emailToUid").child(encodeEmail(calleeEmail)).get().addOnSuccessListener { snapshot ->
        val calleeUid = snapshot.getValue(String::class.java)

        if (calleeUid != null) {
            val callRef = db.child("calls").child(calleeUid)
            val callData = mapOf(
                "from" to currentUid,
                "type" to "video",
                "status" to "ringing"
            )

            callRef.setValue(callData).addOnSuccessListener {
                val intent = Intent(context, VideoCallActivity::class.java).apply {
                    putExtra("isCaller", true)
                    putExtra("calleeUid", calleeUid)
                }
                context.startActivity(intent)
            }
        } else {
            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Failed to initiate call", Toast.LENGTH_SHORT).show()
    }
}

fun encodeEmail(email: String): String {
    return email.replace(".", ",")
}
