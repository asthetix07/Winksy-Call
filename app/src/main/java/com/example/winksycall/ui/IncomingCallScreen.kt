package com.example.winksycall.ui

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.winksycall.viewmodels.CallViewModel

@Composable
fun IncomingCallScreen(
    callViewModel: CallViewModel,
    roomId: String,
    fromUid: String,
    callType: String,
    navController: NavController
) {
    val context = LocalContext.current

    // Hold ringtone instance
    var ringtone: Ringtone? by remember { mutableStateOf(null) }

    // Start ringtone when screen opens
    LaunchedEffect(Unit) {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone?.play()
        callViewModel.startIncomingCallTimeout {
            ringtone?.stop()
            Toast.makeText(context, "You missed a call", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    // Stop ringtone when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            ringtone?.stop()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = if (callType == "video") "Incoming Video Call" else "Incoming Audio Call",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Button(
                    onClick = {
                        ringtone?.stop()
                        callViewModel.cancelIncomingCallTimeout()
                        callViewModel.clearIncomingOffer()
                        navController.navigate("video_call/$roomId/$callType/false")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Accept")
                }

                Button(
                    onClick = {
                        ringtone?.stop()
                        callViewModel.cancelIncomingCallTimeout()
                        callViewModel.stopListeningForIncomingOffers()
                        callViewModel.clearIncomingOffer()
                        Toast.makeText(context, "Call Rejected", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reject")
                }
            }
        }
    }
}
