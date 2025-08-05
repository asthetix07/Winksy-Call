package com.example.winksycall.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.winksycall.database.Contact
import com.example.winksycall.ui.ContactCard
import com.example.winksycall.ui.theme.DeepBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Composable
fun HomeScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val contactsList = remember { mutableStateListOf<Contact>() }
    val context = LocalContext.current
    val callViewModel: CallViewModel = viewModel()
    val navController = rememberNavController() // if not already present


    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val contactsRef = FirebaseDatabase.getInstance().getReference("users/$userId/contacts")

        contactsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactsList.clear()
                snapshot.children.mapNotNullTo(contactsList) {
                    it.getValue(Contact::class.java)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to read contacts", error.toException())
            }
        })
    }


    Box(modifier = Modifier.fillMaxSize().navigationBarsPadding().statusBarsPadding()) {
        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            LazyColumn {
                items(contactsList) { contact ->
                    ContactCard(
                        name = contact.name,
                        email = contact.email,
                        isOnline = true,
                        onCallClick = { email ->
                            callViewModel.startCallWithEmail(
                                calleeEmail = email,
                                onRoomCreated = { roomId ->
                                    // 🔁 Navigate to CallScreen with roomId
                                    navController.navigate("call/$roomId")
                                },
                                onError = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                }
                            )

                        },
                        onVideoCallClick = { /* TODO */ }
                    )
                }
            }
            Text(
                text = "Add user info click + ",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Floating Action Button (bottom end)
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = DeepBlue,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }

        // Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add User Info") },
                shape = RoundedCornerShape(8.dp),
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                cursorColor = colorScheme.primary,
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.secondary,
                                focusedLabelColor = colorScheme.primary
                            ),
                            textStyle = TextStyle(color = colorScheme.onSurface),
                            placeholder = { Text("Name",color = Color.LightGray) },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                cursorColor = colorScheme.primary,
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.secondary,
                                focusedLabelColor = colorScheme.primary
                            ),
                            textStyle = TextStyle(color = colorScheme.onSurface),
                            placeholder = { Text("example@gmail.com",color = Color.LightGray) },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                            val database = FirebaseDatabase.getInstance().reference

                            val newContactId = database.child("users").child(userId).child("contacts").push().key ?: return@Button

                            val contact = Contact(name = name, email = email, id = newContactId)

                            database.child("users").child(userId).child("contacts").child(newContactId)
                                .setValue(contact)
                                .addOnSuccessListener {
                                    showDialog = false
                                    name = ""
                                    email = ""
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to add contact", Toast.LENGTH_SHORT).show()
                                }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
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

