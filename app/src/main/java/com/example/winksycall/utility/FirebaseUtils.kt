package com.example.winksycall.utility

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirebaseUtils {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()


    fun encodeEmail(email: String): String {
        return email.replace(".", ",")
    }

    fun decodeEmail(encoded: String): String {
        return encoded.replace(",", ".")
    }

    fun getUidFromEmail(email: String): Flow<String?> = callbackFlow {
        val safeEmail = encodeEmail(email)

        val ref = FirebaseDatabase.getInstance().getReference("emailToUid/$safeEmail")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(String::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(null)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun userPresenceFlow(uid: String): Flow<Boolean> = callbackFlow {
        val ref = FirebaseDatabase.getInstance().getReference("presence/$uid")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Boolean::class.java) ?: false)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }

    }

    @SuppressLint("SuspiciousIndentation")
    fun setUserOnline(uid: String) {
        val presenceRef = database.getReference("presence/$uid")
            presenceRef.setValue(true)
        //When client disconnect , auto set to false
        presenceRef.onDisconnect().setValue(false)
    }

    fun setUserOffline(uid: String) {
        database.getReference("presence/$uid").setValue(false)
    }



    fun updateOnlineStatus(isOnline: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val presenceRef = database.getReference("presence/$uid")
        presenceRef.setValue(isOnline)
        if (isOnline) {
            // Auto set false when user disconnects (app killed, network lost etc.)
            presenceRef.onDisconnect().setValue(false)
        }
    }
}