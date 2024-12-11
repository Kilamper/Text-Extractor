package com.example.textextractor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var currentUser: FirebaseUser? = null
    private val db = FirebaseFirestore.getInstance()

    private var currentPhotoPath: String? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectImageLauncher: ActivityResultLauncher<String>

    private var cameraImage by mutableStateOf<Bitmap?>(null)
    private var resultText by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentUser = FirebaseAuth.getInstance().currentUser

        cameraImage = AppCompatResources.getDrawable(this, R.drawable.baseline_image_24)!!.toBitmap()

        // Capture image permission launcher
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                captureImage()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        // Take picture launcher
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoPath?.let { path ->
                    val bitmap = BitmapFactory.decodeFile(path)
                    cameraImage = bitmap
                    recognizeText(bitmap)
                }
            }
        }

        // Select image launcher
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                cameraImage = bitmap
                recognizeText(bitmap)
            }
        }

        setContent {
            MainScreen(
                currentUser,
                ::goToActivity,
                requestPermissionLauncher,
                selectImageLauncher,
                cameraImage!!,
                resultText,
                db
            )
        }
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun captureImage() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: Exception) {
            Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", it)
            takePictureLauncher.launch(photoURI)
        }
    }

    private fun recognizeText(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image).addOnSuccessListener { ocrText ->
            resultText = ocrText.text
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to recognize text: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MainScreen(
    currentUser: FirebaseUser?,
    goToActivity: (Class<*>) -> Unit,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    selectImageLauncher: ActivityResultLauncher<String>,
    cameraImage: Bitmap,
    resultText: String,
    db: FirebaseFirestore
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    val isDarkTheme by remember { mutableStateOf(sharedPreferences.getBoolean("dark_theme", false)) }
    var selectedLanguage by remember { mutableStateOf(sharedPreferences.getString("language", "en") ?: "en") }

    LaunchedEffect(isDarkTheme) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    Box(
        modifier = Modifier.fillMaxSize().wrapContentHeight()
            .background(colorResource(R.color.background)),
        contentAlignment = Alignment.TopCenter
    ) {
        HeaderBar(
            currentUser = currentUser,
            onLogInClick = { goToActivity(AuthActivity::class.java) },
            goToActivity = goToActivity,
            activityId = 0
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp).padding(16.dp).verticalScroll(
                rememberScrollState()
            )
        ) {
            ImageSection(requestPermissionLauncher, selectImageLauncher, cameraImage)
            ResultSection(resultText, currentUser, db)
        }
    }
}

@Composable
fun ImageSection(
    requestPermissionLauncher: ActivityResultLauncher<String>,
    selectImageLauncher: ActivityResultLauncher<String>,
    cameraImage: Bitmap
) {
    Column(modifier = Modifier.wrapContentHeight()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                bitmap = cameraImage.asImageBitmap(),
                contentDescription = stringResource(R.string.camera_image),
                modifier = Modifier.size(180.dp).padding(top = 8.dp)
            )
        }
        Button(
            onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) },
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.light_purple))
        ) {
            Text(text = stringResource(R.string.capture_image))
        }
        Button(
            onClick = { selectImageLauncher.launch("image/*") },
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.light_purple))
        ) {
            Text(text = stringResource(R.string.select_image))
        }
    }
}

@Composable
fun ResultSection(resultText: String, currentUser: FirebaseUser?, db: FirebaseFirestore) {
    val context = LocalContext.current
    var editableText by remember { mutableStateOf(resultText) }
    var showAlert by remember { mutableStateOf(false) }

    LaunchedEffect(resultText) {
        editableText = resultText
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            text = { Text(text = stringResource(R.string.limit_toast)) },
            confirmButton = {
                Button(
                    onClick = { showAlert = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.light_purple))
                ) {
                    Text(text = stringResource(R.string.confirm))
                }
            }
        )
    }

    Column(modifier = Modifier.wrapContentHeight()) {
        Text(
            text = stringResource(R.string.result_text),
            style = MaterialTheme.typography.headlineSmall,
            color = colorResource(R.color.text_color),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.height(250.dp).fillMaxWidth()
        ) {
            TextField(
                value = editableText,
                onValueChange = { editableText = it },
                modifier = Modifier.padding(4.dp).padding(vertical = 2.dp)
                    .fillMaxWidth().verticalScroll(rememberScrollState()),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedTextColor = colorResource(R.color.text_color),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                )
            )
        }
        if (editableText.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                Button(
                    onClick = {
                        val clipboard = ContextCompat.getSystemService(context, android.content.ClipboardManager::class.java)
                        val clip = android.content.ClipData.newPlainText("recognized text", editableText)
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(context, R.string.copy_toast, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth().weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.light_purple))
                ) {
                    Text(text = stringResource(R.string.copy_text))
                }
                if (currentUser != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            val userDocRef = db.collection("users").document(currentUser.uid).collection("scannedTexts")
                            userDocRef.get().addOnSuccessListener { documents ->
                                if (documents.size() >= 30) {
                                    showAlert = true
                                } else {
                                    userDocRef.add(mapOf("text" to editableText, "date" to Date.from(java.time.Instant.now())))
                                        .addOnSuccessListener {
                                            Toast.makeText(context, R.string.save_toast, Toast.LENGTH_SHORT).show()
                                        }.addOnFailureListener { e ->
                                            Toast.makeText(context, "Failed to save text: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp).fillMaxWidth().weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.light_purple))
                    ) {
                        Text(text = stringResource(R.string.save_text))
                    }
                }
            }
        }
    }
}