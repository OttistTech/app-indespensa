package com.ottistech.indespensa.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ottistech.indespensa.data.repository.UserRepository

class SplashScreen : AppCompatActivity() {

    private lateinit var splashScreen: SplashScreen
    private val userRepository: UserRepository by lazy {
        UserRepository(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {true}
        val isUserAuthenticated = userRepository.isAuthenticated()
        openNextActivity(isUserAuthenticated)
        finish()
    }

    private fun openNextActivity(isUserAuthenticated: Boolean) {
        val intent = if(isUserAuthenticated) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, AuthActivity::class.java)
        }
        startActivity(intent)
    }
}