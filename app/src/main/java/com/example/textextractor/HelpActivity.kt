package com.example.textextractor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        setTheme(R.style.Theme_TextExtractor)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
