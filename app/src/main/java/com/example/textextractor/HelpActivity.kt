package com.example.textextractor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuInflater
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast

class HelpActivity : AppCompatActivity() {

    private lateinit var menuBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        menuBtn = findViewById(R.id.menuBtn)

        // Menu button
        menuBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, menuBtn)
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.menu_options, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        goToMain()
                        true
                    }
                    R.id.settings -> {
                        Toast.makeText(this, "Opción 1 seleccionada", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.history -> {
                        goToHistory()
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
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun goToHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }
}
