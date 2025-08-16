package com.example.winksycall.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.winksycall.model.Contact
import com.example.winksycall.ui.theme.DeepBlue
import com.example.winksycall.utility.FirebaseUtils
import com.example.winksycall.utility.FirebaseUtils.updateOnlineStatus
import com.example.winksycall.viewmodels.CallViewModel
import com.example.winksycall.viewmodels.CallViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val callViewModel: CallViewModel =
        viewModel(factory = CallViewModelFactory(context.applicationContext))

    val contactsList = remember { mutableStateListOf<Contact>() }

    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        val uid = userId ?: return@LaunchedEffect

        // Fetch contacts
        FirebaseDatabase.getInstance().getReference("users/$uid/contacts")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    contactsList.clear()
                    snapshot.children.mapNotNullTo(contactsList) { it.getValue(Contact::class.java) }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error fetching contacts", error.toException())
                }
            })

        // Update this user's online status
        updateOnlineStatus(true)

        // Listen for incoming offers
        callViewModel.listenForIncomingOffers { roomId, type, fromUid ->
                navController.navigate("incoming_call/$roomId/$type/$fromUid")
         }
    }

    DisposableEffect(Unit) {
        onDispose {
            userId?.let { FirebaseUtils.setUserOffline(it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (contactsList.isEmpty()) {
                Text(
                    text = "Add a contact with +",
                    style = MaterialTheme.typography.headlineSmall
                )
            } else {
                LazyColumn {
                    items(contactsList, key = { it.id }) { contact ->

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart) {
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    if (uid != null) {
                                        FirebaseDatabase.getInstance()
                                            .getReference("users/$uid/contacts/${contact.id}")
                                            .removeValue()
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    true
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent =  {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            },
                            content = {
                                    // Observe UID flow
                                    val currentUid by FirebaseUtils.getUidFromEmail(contact.email)
                                        .collectAsState(initial = null)

                                    // Observe presence flow if UID exists
                                    val presence by remember(currentUid) {
                                        currentUid?.let { FirebaseUtils.userPresenceFlow(it) } ?: flowOf(false)
                                    }.collectAsState(initial = false)

                                    ContactCard(
                                        name = contact.name,
                                        email = contact.email,
                                        isOnline = presence,
                                        onCallClick = {
                                            callViewModel.startCallWithEmail(
                                                calleeEmail = contact.email,
                                                callType = "audio",
                                                onRoomCreated = { roomId ->
                                                    navController.navigate("video_call/$roomId/audio/true")
                                                },
                                                onError = { error ->
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        },
                                        onVideoCallClick = {
                                            callViewModel.startCallWithEmail(
                                                calleeEmail = contact.email,
                                                callType = "video",
                                                onRoomCreated = { roomId ->
                                                    navController.navigate("video_call/$roomId/video/true")
                                                },
                                                onError = { error ->
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    )
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = DeepBlue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Contact") },
                shape = RoundedCornerShape(8.dp),
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                            placeholder = { Text("Name", color = Color.LightGray) },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                            placeholder = { Text("example@gmail.com", color = Color.LightGray) },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                        val contactId = FirebaseDatabase.getInstance().reference
                            .child("users").child(uid).child("contacts").push().key
                            ?: return@Button

                        val contact = Contact(name = name, email = email, id = contactId)

                        FirebaseDatabase.getInstance()
                            .getReference("users/$uid/contacts/$contactId")
                            .setValue(contact)
                            .addOnSuccessListener {
                                showDialog = false
                                name = ""
                                email = ""
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed to add contact", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
