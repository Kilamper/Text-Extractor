package com.example.textextractor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString

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
                                val scannedText = document.toObject(ScannedText::class.java)
                                scannedTexts.add(scannedText)
                            }
                        }
                }
            }

            HistoryScreen(scannedTexts)
        }
    }
}

@Composable
fun HistoryScreen(scannedTexts: List<ScannedText>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Text(text = "History", style = MaterialTheme.typography.headlineLarge)
            scannedTexts.forEach { scannedText ->
                TextCard(scannedText)
            }
        }
    }
}

@Composable
fun TextCard(scannedText: ScannedText) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = scannedText.text!!, style = MaterialTheme.typography.bodyLarge)
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
                    onClick = {
                        clipboardManager.setText(AnnotatedString(scannedText.text!!))
                        Toast.makeText(context, R.string.delete_toast, Toast.LENGTH_SHORT).show()
                    },
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