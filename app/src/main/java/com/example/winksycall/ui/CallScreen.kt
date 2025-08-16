package com.example.winksycall.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.winksycall.R
import com.example.winksycall.viewmodels.CallViewModel
import com.example.winksycall.viewmodels.VideoCallViewModel
import org.webrtc.CameraVideoCapturer

@Composable
fun CallScreen(
    viewModel: VideoCallViewModel,
    callType: String,
//    onCallEnded: () -> Unit,
    navController: NavController
) {
    var isMuted by remember { mutableStateOf(false) }
    val callViewModel = CallViewModel()


    DisposableEffect(Unit) {
        // Set Z order to overlay (needed for video rendering stacking)
        viewModel.remoteVideoView.setZOrderMediaOverlay(true)
        viewModel.localVideoView.setZOrderMediaOverlay(true)

        onDispose {
            viewModel.endCall()
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize().navigationBarsPadding().statusBarsPadding()) {
            // Video background if it's a video call
            if (callType == "video") {
                AndroidView(
                    factory = { viewModel.remoteVideoView },
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    AndroidView(
                        factory = { viewModel.localVideoView },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Audio call: just show a placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Audio Call Ongoing", color = Color.White)
                }
            }

            // Call control buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {
                // Mute / Unmute
                IconButton(onClick = {
                    isMuted = !isMuted
                    viewModel.localAudioTrack.setEnabled(!isMuted)
                }) {
                    Icon(
                        painter = if (isMuted) painterResource(id = R.drawable.mic_off_24px) else painterResource(id = R.drawable.mic_24px),
                        contentDescription = "Toggle Mic",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Flip Camera (only for video)
                if (callType == "video") {
                    IconButton(onClick = {
                        if (viewModel.localVideoCapturer is CameraVideoCapturer) {
                            (viewModel.localVideoCapturer as CameraVideoCapturer).switchCamera(null)
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.switch_camera_24px),
                            contentDescription = "Flip Camera",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // End Call
                IconButton(onClick = {
                    viewModel.endCall()
                    callViewModel.stopListeningForIncomingOffers()
                    callViewModel.clearIncomingOffer()
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.call_end_24px),
                        contentDescription = "End Call",
                        tint = Color.Red,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

