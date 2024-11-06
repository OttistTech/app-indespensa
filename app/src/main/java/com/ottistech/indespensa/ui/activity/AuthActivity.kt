package com.ottistech.indespensa.ui.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.ottistech.indespensa.databinding.ActivityAuthBinding
import com.ottistech.indespensa.ui.helpers.clearInputFocus

class AuthActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
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