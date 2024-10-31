package com.ottistech.indespensa.ui.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.ActivityAuthBinding
import com.ottistech.indespensa.ui.helpers.clearInputFocus

class AuthActivity : AppCompatActivity() {

    private val binding : ActivityAuthBinding by lazy {
        ActivityAuthBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val host : NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.auth_nav_host_fragment) as NavHostFragment
        val navController : NavController = host.navController
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        clearInputFocus(
            event,
            currentFocus,
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        )
        return super.dispatchTouchEvent(event)
    }
}