package com.ottistech.indespensa.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.ActivityMainBinding
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.shared.model.AppUserData

class MainActivity : AppCompatActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val currentUser = AppUserData( // TODO: Replace this mock with current user's real info (supposed to be done at task #105)
        userId = 1,
        name = "Davi",
        email = "davi@piassi.com",
        type = AppAccountType.PERSONAL,
        isPremium = false
    )

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

        if(currentUser.type == AppAccountType.PERSONAL) {
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