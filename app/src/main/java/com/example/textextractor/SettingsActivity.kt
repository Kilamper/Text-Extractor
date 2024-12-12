package com.example.textextractor

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = FirebaseAuth.getInstance().currentUser

        val isDarkTheme = isDarkThemeEnabled(this)
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("dark_theme", isDarkTheme).apply()
        val defaultLanguage = Locale.getDefault().language
        if (defaultLanguage in listOf("en", "es", "fr", "it") && sharedPreferences.getString("language", "") == "") {
            editor.putString("language", defaultLanguage).apply()
        }

        setContent {
            SettingsScreen(currentUser, ::goToActivity)
        }
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}

fun isDarkThemeEnabled(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    val userPrefersDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    val isSystemDarkTheme = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES ||
            (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_AUTO &&
                    (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
    return userPrefersDarkTheme || isSystemDarkTheme
}

fun setLocale(context: Context, language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.createConfigurationContext(config)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
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
    ) {
        HeaderBar(
            currentUser = currentUser,
            onLogInClick = { goToActivity(AuthActivity::class.java) },
            goToActivity = goToActivity,
            activityId = 2
        )

        Column(
            modifier = Modifier.padding(top = 50.dp).padding(16.dp).verticalScroll(
                rememberScrollState()
            ).fillMaxSize()
        ) {
            ThemeSection(isDarkTheme) { newTheme ->
                isDarkTheme = newTheme
                editor.putBoolean("dark_theme", newTheme).apply()
            }

            Spacer(modifier = Modifier.height(16.dp))

            LanguageSection(selectedLanguage) { newLanguage ->
                selectedLanguage = newLanguage
                editor.putString("language", newLanguage).apply()
                setLocale(context, newLanguage)
            }
        }
    }
}

@Composable
fun ThemeSection(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    Text(
        text = stringResource(R.string.settings),
        style = MaterialTheme.typography.headlineMedium,
        color = colorResource(R.color.text_color)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(200.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.dark_theme),
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.text_color)
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = {
                onThemeChange(it)
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = colorResource(R.color.light_purple)
            )
        )
    }
}

@Composable
fun LanguageSection(selectedLanguage: String, onLanguageChange: (String) -> Unit) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.width(200.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.text_color)
        )
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
                containerColor = colorResource(R.color.menu_color)
            ) {
                DropdownMenuItem(
                    onClick = {
                        onLanguageChange("en")
                        isDropdownExpanded = false
                    },
                    text = { Text("English") },
                    colors = MenuDefaults.itemColors(
                        textColor = colorResource(R.color.text_color)
                    )
                )
                DropdownMenuItem(
                    onClick = {
                        onLanguageChange("es")
                        isDropdownExpanded = false
                    },
                    text = { Text("Español") },
                    colors = MenuDefaults.itemColors(
                        textColor = colorResource(R.color.text_color)
                    )
                )
                DropdownMenuItem(
                    onClick = {
                        onLanguageChange("fr")
                        isDropdownExpanded = false
                    },
                    text = { Text("Français") },
                    colors = MenuDefaults.itemColors(
                        textColor = colorResource(R.color.text_color)
                    )
                )
                DropdownMenuItem(
                    onClick = {
                        onLanguageChange("it")
                        isDropdownExpanded = false
                    },
                    text = { Text("Italiano") },
                    colors = MenuDefaults.itemColors(
                        textColor = colorResource(R.color.text_color)
                    )
                )
                DropdownMenuItem(
                    onClick = {
                        onLanguageChange("de")
                        isDropdownExpanded = false
                    },
                    text = { Text("Deutsch") },
                    colors = MenuDefaults.itemColors(
                        textColor = colorResource(R.color.text_color)
                    )
                )
                DropdownMenuItem(
                    onClick = {
                        onLanguageChange("pt")
                        isDropdownExpanded = false
                    },
                    text = { Text("Português") },
                    colors = MenuDefaults.itemColors(
                        textColor = colorResource(R.color.text_color)
                    )
                )
            }
        }
    }
}