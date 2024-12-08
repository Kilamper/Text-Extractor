package com.example.textextractor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import java.text.DateFormat
import java.util.Locale

class HistoryActivity : ComponentActivity() {

    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scannedTexts = remember { mutableStateListOf<ScannedText>() }
            currentUser = FirebaseAuth.getInstance().currentUser
            val userId = currentUser?.uid

            LaunchedEffect(userId) {
                if (userId != null) {
                    FirebaseFirestore.getInstance()
                        .document("users/$userId")
                        .collection("scannedTexts")
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                val scannedText = document.toObject(ScannedText::class.java).apply {
                                    id = document.id
                                }
                                scannedTexts.add(scannedText)
                            }
                        }
                }
            }

            HistoryScreen(scannedTexts, currentUser, ::goToActivity)
        }
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}

@Composable
fun HistoryScreen(scannedTexts: List<ScannedText>, currentUser: FirebaseUser?, goToActivity: (Class<*>) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        HeaderBar(
            currentUser = currentUser,
            onLogInClick = { goToActivity(AuthActivity::class.java) },
            goToActivity = goToActivity
        )
        Column(
            modifier = Modifier.padding(top = 40.dp).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            scannedTexts.forEach { scannedText ->
                TextCard(scannedText, currentUser, scannedTexts.toMutableList())
            }
        }
    }
}

@Composable
fun HeaderBar(
    currentUser: FirebaseUser?,
    onLogInClick: () -> Unit,
    goToActivity: (Class<*>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(colorResource(id = R.color.light_purple))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BurgerMenu(goToActivity)

        Spacer(modifier = Modifier.weight(1f))

        if (currentUser != null) {
            UserMenu(currentUser, goToActivity)
        } else {
            Button(
                onClick = onLogInClick,
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.light_purple)),
                modifier = Modifier.height(50.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.log_in),
                    color = colorResource(id = R.color.white)
                )
            }
        }
    }
}

@Composable
fun BurgerMenu(goToActivity: (Class<*>) -> Unit) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded.value = true },
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_menu_24),
            tint = Color.White,
            contentDescription = null
        )
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.home)) },
            onClick = {
                goToActivity(MainActivity::class.java)
                expanded.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.settings)) },
            onClick = {
                Toast.makeText(context, "Opción 1 seleccionada", Toast.LENGTH_SHORT).show()
                expanded.value = false
            })
        DropdownMenuItem(
            text = { Text(stringResource(R.string.help)) },
            onClick = {
                goToActivity(HelpActivity::class.java)
                expanded.value = false
            })
    }
}

@Composable
fun UserMenu(currentUser: FirebaseUser?, goToActivity: (Class<*>) -> Unit) {
    val expanded = remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded.value = true },
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_person_24),
            tint = Color.White,
            contentDescription = null
        )
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            text = { Text(currentUser?.email ?: "") },
            onClick = {
                expanded.value = false
            }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.log_out)) },
            onClick = {
                FirebaseAuth.getInstance().signOut()
                expanded.value = false
                goToActivity(MainActivity::class.java)
            }
        )
    }
}

@Composable
fun TextCard(scannedText: ScannedText, currentUser: FirebaseUser?, scannedTexts: MutableList<ScannedText>) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.confirm_delete_title)) },
            text = { Text(text = stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseFirestore.getInstance()
                            .document("users/${currentUser!!.uid}")
                            .collection("scannedTexts")
                            .document(scannedText.id!!)
                            .delete()
                            .addOnSuccessListener {
                                scannedTexts.remove(scannedText)
                                Toast.makeText(context, R.string.delete_toast, Toast.LENGTH_SHORT).show()
                            }
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, Locale.getDefault())
    val formattedDate = dateFormat.format(scannedText.date!!)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(end = 8.dp).fillMaxHeight()) {
                Text(
                    text = scannedText.text!!,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formattedDate.toString().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Column {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(scannedText.text!!))
                        Toast.makeText(context, R.string.copy_toast, Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_content_copy_24),
                        colorFilter = ColorFilter.tint(Color.Black),
                        contentDescription = "Copy",
                    )
                }
                Button(
                    onClick = { showDialog.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_delete_outline_24),
                        contentDescription = "Delete",
                    )
                }
            }
        }
    }
}