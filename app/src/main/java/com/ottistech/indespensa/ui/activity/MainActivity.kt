package com.ottistech.indespensa.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.ActivityMainBinding
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.ui.helpers.getCurrentUser

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val host : NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController : NavController = host.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.scanner_dest -> changeBottomNavigationBarVisibility(View.GONE)
                else -> changeBottomNavigationBarVisibility(View.VISIBLE)
            }
        }

        if(getCurrentUser().type == AppAccountType.PERSONAL) {
            binding.mainBottomNavigation.inflateMenu(R.menu.bottom_nav_menu_personal)
            navController.setGraph(R.navigation.main_personal_navigation)
        } else {
            binding.mainBottomNavigation.inflateMenu(R.menu.bottom_nav_menu_enterprise)
            navController.setGraph(R.navigation.main_enterprise_navigation)
        }
        binding.mainBottomNavigation.setupWithNavController(navController)
    }

    private fun changeBottomNavigationBarVisibility(visibility: Int) {
        binding.mainBottomNavigation.visibility = visibility
    }
}