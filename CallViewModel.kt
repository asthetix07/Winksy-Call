package com.example.winksycall.auth



import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class CallViewModel : ViewModel() {

    fun startCallWithEmail(
        calleeEmail: String,
        onRoomCreated: (roomId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val callerUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val safeEmail = calleeEmail.replace(".", "_").replace("@", "_")
        val emailToUidRef = FirebaseDatabase.getInstance().getReference("emailToUid/$safeEmail")

        emailToUidRef.get().addOnSuccessListener { snapshot ->
            val calleeUid = snapshot.getValue(String::class.java)

            if (calleeUid != null) {
                val roomId = UUID.randomUUID().toString()
                val callRef = FirebaseDatabase.getInstance().getReference("calls/$roomId")

                val callData = mapOf(
                    "caller" to callerUid,
                    "callee" to calleeUid,
                    "status" to "calling"
                )

                callRef.setValue(callData).addOnSuccessListener {
                    onRoomCreated(roomId)
                }.addOnFailureListener {
                    onError("Failed to create call room")
                }
            } else {
                onError("User not found")
            }
        }.addOnFailureListener {
            onError("Failed to lookup email")
        }
    }
}
