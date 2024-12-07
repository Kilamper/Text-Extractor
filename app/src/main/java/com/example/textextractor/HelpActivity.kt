package com.example.textextractor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuInflater
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HelpActivity : AppCompatActivity() {

    private lateinit var menuBtn: Button
    private lateinit var logInBtn: Button
    private lateinit var userIcon: Button
    private var currentUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        menuBtn = findViewById(R.id.menuBtn)
        logInBtn = findViewById(R.id.logInBtn)
        userIcon = findViewById(R.id.userIcon)

        // Menu button
        menuBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, menuBtn)
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        goToActivity(MainActivity::class.java)
                        true
                    }
                    R.id.settings -> {
                        Toast.makeText(this, "Opción 1 seleccionada", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.history -> {
                        goToActivity(HistoryActivity::class.java)
                        true
                    }

                    R.id.help -> {
                        val intent = Intent(this, HelpActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

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

        // Log in button
        logInBtn.setOnClickListener {
            goToActivity(AuthActivity::class.java)
        }
    }

    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}
