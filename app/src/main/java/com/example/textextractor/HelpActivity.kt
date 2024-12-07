package com.example.textextractor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

class HelpActivity : AppCompatActivity() {

    private lateinit var menuBtn: Button
    private lateinit var logInBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        menuBtn = findViewById(R.id.menuBtn)
        logInBtn = findViewById(R.id.logInBtn)

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
