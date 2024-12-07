package com.example.textextractor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.CoroutineScope

class AuthActivity : ComponentActivity() {

    private val GOOGLE_SIGN_IN = 133

    private lateinit var snackbarHostState: SnackbarHostState
    private lateinit var coroutineScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            snackbarHostState = remember { SnackbarHostState() }
            coroutineScope = rememberCoroutineScope()

            AuthScreen(
                onSignUp = { email, password -> signUp(email, password, snackbarHostState, coroutineScope) },
                onLogIn = { email, password -> logIn(email, password, snackbarHostState, coroutineScope) },
                onGoogleSignIn = { googleSignIn() },
                onClose = { finish() },
                snackbarHostState = snackbarHostState
            )
        }
    }

    private fun signUp(email: String, password: String, snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToMain()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Sign up failed: ${it.exception?.message}")
                        }
                    }
                }
        }
    }

    private fun logIn(email: String, password: String, snackbarHostState: SnackbarHostState, coroutineScope: CoroutineScope) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToMain()
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Log in failed: ${it.exception?.message}")
                        }
                    }
                }
        }
    }

    private fun googleSignIn() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()

        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    @Deprecated("This method is deprecated")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                goToMain()
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Google sign in failed: ${it.exception?.message}")
                                }
                            }
                        }
                }
            } catch (e: ApiException) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Google sign in failed: ${e.message}")
                }
            }
        }
    }
}

@Composable
fun AuthScreen(
    onSignUp: (String, String) -> Unit,
    onLogIn: (String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onClose: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.dark_purple)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_icon),
                contentDescription = stringResource(id = R.string.app_icon),
                modifier = Modifier.size(180.dp, 160.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.welcome),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.log_in_to_continue),
                color = Color(0xFFD0D0D0),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            EmailTextField(
                email = email,
                onEmailChange = { email = it },
                emailFocusRequester = emailFocusRequester,
                isEmailFocused = isEmailFocused,
                onEmailFocusChange = { isEmailFocused = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                password = password,
                onPasswordChange = { password = it },
                passwordFocusRequester = passwordFocusRequester,
                isPasswordFocused = isPasswordFocused,
                onPasswordFocusChange = { isPasswordFocused = it }
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onSignUp(email, password) },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(text = stringResource(id = R.string.sign_up), color = Color.White)
                }
                Button(
                    onClick = { onLogIn(email, password) },
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07B3F))
                ) {
                    Text(text = stringResource(id = R.string.log_in_button), color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onGoogleSignIn() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = stringResource(id = R.string.google_button),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.google_button), color = Color.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onClose() },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.light_gray))
            ) {
                Text(text = stringResource(R.string.return_back), color = Color.White)
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Composable
fun EmailTextField(
    email: String,
    onEmailChange: (String) -> Unit,
    emailFocusRequester: FocusRequester,
    isEmailFocused: Boolean,
    onEmailFocusChange: (Boolean) -> Unit
) {
    BasicTextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp)
            .focusRequester(emailFocusRequester)
            .onFocusChanged { onEmailFocusChange(it.isFocused) },
        singleLine = true,
        textStyle = TextStyle(color = colorResource(id = R.color.light_gray), textAlign = TextAlign.Start),
        cursorBrush = SolidColor(colorResource(id = R.color.light_gray)),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
            ) {
                if (email.isEmpty() && !isEmailFocused) {
                    Text(
                        text = stringResource(id = R.string.email),
                        color = colorResource(id = R.color.light_gray),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordFocusRequester: FocusRequester,
    isPasswordFocused: Boolean,
    onPasswordFocusChange: (Boolean) -> Unit
) {
    BasicTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp)
            .focusRequester(passwordFocusRequester)
            .onFocusChanged { onPasswordFocusChange(it.isFocused) },
        singleLine = true,
        textStyle = TextStyle(color = colorResource(id = R.color.light_gray), textAlign = TextAlign.Start),
        cursorBrush = SolidColor(colorResource(id = R.color.light_gray)),
        visualTransformation = PasswordVisualTransformation(),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
            ) {
                if (password.isEmpty() && !isPasswordFocused) {
                    Text(
                        text = stringResource(id = R.string.password),
                        color = colorResource(id = R.color.light_gray),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                innerTextField()
            }
        }
    )
}