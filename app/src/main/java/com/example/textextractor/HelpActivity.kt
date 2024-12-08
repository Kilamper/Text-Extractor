package com.example.textextractor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HelpActivity : ComponentActivity() {

    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = FirebaseAuth.getInstance().currentUser

        setContent {
            HelpScreen(currentUser, ::goToActivity)
        }
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}

@Composable
fun HelpScreen(currentUser: FirebaseUser?, goToActivity: (Class<*>) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        HeaderBar(
            currentUser = currentUser,
            onLogInClick = { goToActivity(AuthActivity::class.java) },
            goToActivity = goToActivity,
            activityId = 3
        )
        Column(
            Modifier.padding(top = 40.dp).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            FAQSection()
        }
    }
}

@Composable
fun FAQSection() {
    Column {
        Text(
            text = stringResource(id = R.string.faq),
            style = MaterialTheme.typography.headlineLarge,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FAQItem(
            question = "1. How do I take a picture?",
            answer = "In order to take a picture, you have to click on 'Capture Image' and give camera permissions"
        )
        FAQItem(
            question = "2. Can I select a picture from my gallery?",
            answer = "Yes, you can do it by clicking on 'Select Image'."
        )
        FAQItem(
            question = "3. How can I scan the text from an image?",
            answer = "The scanned text will automatically appear when you select or take a picture."
        )
        FAQItem(
            question = "4. Can I edit the scanned text?",
            answer = "Yes, just click on it and you will be able to edit it like normal."
        )
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(id = R.color.black)
        )
    }
}