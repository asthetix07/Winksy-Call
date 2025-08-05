package com.example.winksycall.utility

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.webrtc.SurfaceViewRenderer // optional if you later add WebRTC video views

class VideoCallActivity : ComponentActivity() {

    private lateinit var callerUid: String
    private lateinit var calleeUid: String
    private var isCaller: Boolean = false
    private lateinit var callRef: DatabaseReference
    private lateinit var currentUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return finish()
        isCaller = intent.getBooleanExtra("isCaller", false)
        calleeUid = intent.getStringExtra("calleeUid") ?: return finish()

        // Determine which path to listen to (caller/callee)
        val otherUserUid = if (isCaller) calleeUid else currentUid
        callRef = FirebaseDatabase.getInstance().getReference("calls").child(currentUid)

        // Start call listener
        listenForCallUpdates()

        // UI rendering can go here later (use Jetpack Compose or setContentView)
        Toast.makeText(this, "Video Call Started with $calleeUid", Toast.LENGTH_SHORT).show()
    }

    private fun listenForCallUpdates() {
        callRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val callStatus = snapshot.child("status").getValue(String::class.java)

                when (callStatus) {
                    "accepted" -> {
                        // Setup WebRTC PeerConnection here
                        Toast.makeText(this@VideoCallActivity, "Call accepted", Toast.LENGTH_SHORT).show()
                        Log.d("VideoCall", "Call accepted")
                    }
                    "declined" -> {
                        Toast.makeText(this@VideoCallActivity, "Call declined", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    "ended" -> {
                        Toast.makeText(this@VideoCallActivity, "Call ended", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("VideoCall", "Call status listener cancelled", error.toException())
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up the call (optional depending on role)
        if (isCaller) {
            callRef.child("status").setValue("ended")
        }
    }
}
