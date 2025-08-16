package com.example.winksycall.ui

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.winksycall.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactCard(
    name: String,
    email: String,
    isOnline: Boolean,
    onCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )

    fun requestPermissionsAndRun(action: () -> Unit) {
        if (permissionsState.allPermissionsGranted) {
            action()
        } else {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Name and Email
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }

            // Middle + Right: Buttons + Status dot
            Row(
                modifier = Modifier.padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { requestPermissionsAndRun(onCallClick) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.call_24px),
                        contentDescription = "Call"
                    )
                }
                IconButton(
                    onClick = { requestPermissionsAndRun(onVideoCallClick) }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.video_call_24px),
                        contentDescription = "Video Call"
                    )
                }
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Gray, CircleShape)
                        .background(if (isOnline) Color.Green else Color.Red)
                )
            }
        }
    }
}
