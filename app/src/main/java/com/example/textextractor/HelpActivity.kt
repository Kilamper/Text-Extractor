package com.example.textextractor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HelpActivity : AppCompatActivity() {

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
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    val isDarkTheme by remember { mutableStateOf(sharedPreferences.getBoolean("dark_theme", false)) }
    var selectedLanguage by remember { mutableStateOf(sharedPreferences.getString("language", "en") ?: "en") }

    LaunchedEffect(isDarkTheme) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    LaunchedEffect(selectedLanguage) {
        setLocale(context, selectedLanguage)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(colorResource(R.color.background)),
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
            color = colorResource(id = R.color.text_color),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        for (i in 1..5) {
            FAQItem(
                question = stringResource(id = R.string::class.java.getField("question_$i").getInt(null)),
                answer = stringResource(id = R.string::class.java.getField("answer_$i").getInt(null))
            )
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyLarge,
            color = colorResource(id = R.color.text_color),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(id = R.color.text_color)
        )
    }
}