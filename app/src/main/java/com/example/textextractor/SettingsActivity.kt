package com.example.textextractor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class SettingsActivity : ComponentActivity() {

    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = FirebaseAuth.getInstance().currentUser

        setContent {
            SettingsScreen(currentUser, ::goToActivity)
        }
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}

@Composable
fun SettingsScreen(
    currentUser: FirebaseUser?,
    goToActivity: (Class<*>) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    var isDarkTheme by remember { mutableStateOf(sharedPreferences.getBoolean("dark_theme", false)) }
    var selectedLanguage by remember { mutableStateOf(sharedPreferences.getString("language", "en") ?: "en") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        HeaderBar(
            currentUser = currentUser,
            onLogInClick = { goToActivity(AuthActivity::class.java) },
            goToActivity = goToActivity,
            activityId = 2
        )

        Column(
            modifier = Modifier.padding(top = 40.dp).padding(16.dp).verticalScroll(
                rememberScrollState()
            )
        ) {
            Text(text = stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.dark_theme))
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {
                        isDarkTheme = it
                        editor.putBoolean("dark_theme", it).apply()
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = colorResource(R.color.light_purple)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.language))
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    Button(
                        onClick = { isDropdownExpanded = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_purple)
                        ),
                    ) {
                        Text(text = selectedLanguage)
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                selectedLanguage = "en"
                                editor.putString("language", "en").apply()
                                isDropdownExpanded = false
                            },
                            text = { Text("English") }
                        )
                        DropdownMenuItem(
                            onClick = {
                                selectedLanguage = "es"
                                editor.putString("language", "es").apply()
                                isDropdownExpanded = false
                            },
                            text = { Text("Español") }
                        )
                    }
                }
            }
        }
    }
}