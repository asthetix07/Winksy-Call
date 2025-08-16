package com.example.winksycall.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.winksycall.utility.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CallViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var callTimeoutJob: Job? = null

    // Start 30s timeout for incoming call
    fun startIncomingCallTimeout(onTimeout: () -> Unit) {
        callTimeoutJob?.cancel()
        callTimeoutJob = viewModelScope.launch {
            delay(30_000) // 30 seconds
            clearIncomingOffer()
            stopListeningForIncomingOffers()
            onTimeout()
        }
    }

    // Cancel timeout if user accepts/rejects before 30s
    fun cancelIncomingCallTimeout() {
        callTimeoutJob?.cancel()
    }


    private fun getUidByEmail(calleeEmail: String, onResult: (String?) -> Unit) {
        val safeEmail = FirebaseUtils.encodeEmail(calleeEmail)
        val ref = FirebaseDatabase.getInstance().getReference("emailToUid/$safeEmail")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onResult(snapshot.getValue(String::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null)
            }
        })
    }

    fun startCallWithEmail(
        calleeEmail: String,
        callType: String, // "audio" or "video"
        onRoomCreated: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        getUidByEmail(calleeEmail) { calleeUid ->
            if (calleeUid.isNullOrEmpty()) {
                onError("User UID not found.")
                return@getUidByEmail
            }

            val roomId = database.getReference("calls").push().key
            if (roomId.isNullOrEmpty()) {
                onError("Failed to create room ID.")
                return@getUidByEmail
            }

            val callerUid = auth.currentUser?.uid
            if (callerUid.isNullOrEmpty()) {
                onError("Caller not authenticated.")
                return@getUidByEmail
            }

            val offerRef = database.getReference("offers/$calleeUid")
            val offer = mapOf(
                "roomId" to roomId,
                "from" to callerUid,
                "type" to callType
            )

            offerRef.setValue(offer)
                .addOnSuccessListener { onRoomCreated(roomId) }
                .addOnFailureListener { onError("Failed to send offer.") }
        }
    }

    private var offerListener: ValueEventListener? = null

    fun listenForIncomingOffers(
        onOfferReceived: (roomId: String, type: String, fromUid: String) -> Unit,
//        onOfferCleared: () -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        val offerRef = database.getReference("offers/$uid")

        offerListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() || snapshot.value == null) {
                    // No active offer — do nothing
//                    onOfferCleared()
                    return
                }

                val offerMap = snapshot.value as? Map<*, *> ?: return
                val roomId = offerMap["roomId"] as? String ?: return
                val fromUid = offerMap["from"] as? String ?: return
                val type = offerMap["type"] as? String ?: "audio"

                if (roomId.isNotEmpty() && fromUid.isNotEmpty()) {
                    onOfferReceived(roomId, type, fromUid)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional: log or handle Firebase error
            }
        }

        offerRef.addValueEventListener(offerListener as ValueEventListener)
    }

    fun stopListeningForIncomingOffers() {
        val uid = auth.currentUser?.uid ?: return
        offerListener?.let {
            database.getReference("offers/$uid").removeEventListener(it)
            offerListener = null
        }
    }

    fun clearIncomingOffer() {
        val uid = auth.currentUser?.uid ?: return
        database.getReference("offers/$uid").removeValue()
    }

}
