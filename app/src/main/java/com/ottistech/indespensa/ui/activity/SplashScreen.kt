package com.ottistech.indespensa.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ottistech.indespensa.R

class SplashScreen : AppCompatActivity() {

    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {true}
        openNextActivity()
        finish()
    }

    fun openNextActivity() {
        startActivity(
            Intent(this, MainActivity::class.java)
        )
    }
}