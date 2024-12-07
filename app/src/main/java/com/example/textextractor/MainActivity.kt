package com.example.textextractor

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.util.TypedValue
import android.view.MenuInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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

    private lateinit var cameraImage: ImageView
    private lateinit var captureImgBtn: Button
    private lateinit var selectImgBtn: Button

    private lateinit var resultText: TextView
    private lateinit var copyTextBtn: Button
    private lateinit var saveTextBtn: Button

    private lateinit var menuBtn: Button
    private lateinit var logInBtn: Button
    private lateinit var userIcon: Button

    private var currentUser: FirebaseUser? = null
    private val db = FirebaseFirestore.getInstance()

    private var currentPhotoPath: String? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var selectImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraImage = findViewById(R.id.cameraImage)
        captureImgBtn = findViewById(R.id.captureImgBtn)
        selectImgBtn = findViewById(R.id.selectImgBtn)

        resultText = findViewById(R.id.resultText)
        copyTextBtn = findViewById(R.id.copyTextBtn)
        saveTextBtn = findViewById(R.id.saveTextBtn)

        menuBtn = findViewById(R.id.menuBtn)
        logInBtn = findViewById(R.id.logInBtn)
        userIcon = findViewById(R.id.userIcon)

        // Setup
        setup()
    }

    private fun setup() {
        title = "Main"

        // Menu button
        menuBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, menuBtn)
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.settings -> {
                        Toast.makeText(this, "Opción 1 seleccionada", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.history -> {
                        goToActivity(HistoryActivity::class.java)
                        true
                    }

                    R.id.help -> {
                        goToActivity(HelpActivity::class.java)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

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
                    cameraImage.setImageBitmap(bitmap)
                    recognizeText(bitmap)
                }
            }
        }

        // Select image launcher
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                cameraImage.setImageBitmap(bitmap)
                recognizeText(bitmap)
            }
        }

        // Capture image button
        captureImgBtn.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // Select image button
        selectImgBtn.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        // Log in button
        logInBtn.setOnClickListener {
            goToActivity(AuthActivity::class.java)
        }

        // Check if user is logged in
        currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            userIcon.visibility = Button.VISIBLE
            logInBtn.visibility = Button.INVISIBLE
            val widthInDp = 50
            val widthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                widthInDp.toFloat(),
                resources.displayMetrics
            ).toInt()
            userIcon.layoutParams.width = widthInPx
            logInBtn.layoutParams.width = 0
            userIcon.setOnClickListener {
                val popupMenu = PopupMenu(this, userIcon)
                val inflater: MenuInflater = menuInflater
                inflater.inflate(R.menu.user_options, popupMenu.menu)

                // Set the user email in the menu
                val userEmailMenuItem = popupMenu.menu.findItem(R.id.userEmail)
                userEmailMenuItem.title = currentUser?.email ?: ""

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.logOut -> {
                            FirebaseAuth.getInstance().signOut()
                            recreate()
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show() // Show the popup menu
            }
        }
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
            resultText.text = ocrText.text
            resultText.movementMethod = ScrollingMovementMethod()
            copyTextBtn.visibility = Button.VISIBLE
            copyTextBtn.setOnClickListener {
                val clipboard = ContextCompat.getSystemService(this, android.content.ClipboardManager::class.java)
                val clip = android.content.ClipData.newPlainText("recognized text", ocrText.text)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(this, "Text Copied", Toast.LENGTH_SHORT).show()
            }
            if (currentUser != null) {
                copyTextBtn.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                saveTextBtn.layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                saveTextBtn.visibility = Button.VISIBLE
                saveTextBtn.setOnClickListener {
                    db.collection("users").document(currentUser!!.uid).collection("scannedTexts").add(mapOf("text" to resultText.text.toString(), "date" to Date.from(java.time.Instant.now())))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save text: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to recognize text: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}
