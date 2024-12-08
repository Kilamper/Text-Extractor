package com.example.textextractor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun HeaderBar(
    currentUser: FirebaseUser?,
    onLogInClick: () -> Unit,
    goToActivity: (Class<*>) -> Unit,
    activityId: Int
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
        BurgerMenu(goToActivity, activityId)

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
fun BurgerMenu(goToActivity: (Class<*>) -> Unit, activityId: Int) {
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
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.padding(end = 8.dp)
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.home)) },
            onClick = {
                goToActivity(MainActivity::class.java)
                expanded.value = false
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_home_filled_24),
                    tint = Color.Black,
                    contentDescription = null
                )
            }
        )
        if (activityId != 1) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.history)) },
                onClick = {
                    goToActivity(HistoryActivity::class.java)
                    expanded.value = false
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_history_24),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
            )
        }
        DropdownMenuItem(
            text = { Text(stringResource(R.string.settings)) },
            onClick = {
                Toast.makeText(context, "Opción 1 seleccionada", Toast.LENGTH_SHORT).show()
                expanded.value = false
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_settings_24),
                    tint = Color.Black,
                    contentDescription = null
                )
            }
        )
        if (activityId != 3) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.help)) },
                onClick = {
                    goToActivity(HelpActivity::class.java)
                    expanded.value = false
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_help_outline_24),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
fun UserMenu(currentUser: FirebaseUser?, goToActivity: (Class<*>) -> Unit) {
    val expanded = remember { mutableStateOf(false) }

    Box {
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
            onDismissRequest = { expanded.value = false },
            offset = DpOffset.Unspecified,
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
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_logout_24),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
            )
        }
    }
}