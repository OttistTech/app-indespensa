package com.ottistech.indespensa.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

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
        // TODO: check if user is authenticated and take the best action in flow
        // TODO: maybe implement navigation here
        startActivity(
            Intent(this, AuthActivity::class.java)
        )
    }
}