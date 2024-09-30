package com.ottistech.indespensa.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.ActivityAuthBinding

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
}