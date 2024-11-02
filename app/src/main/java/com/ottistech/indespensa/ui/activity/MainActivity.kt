package com.ottistech.indespensa.ui.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.ActivityMainBinding
import com.ottistech.indespensa.notification.schedulePantryItemValidityCheck
import com.ottistech.indespensa.shared.AppAccountType
import com.ottistech.indespensa.ui.helpers.clearInputFocus
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

        when (getCurrentUser().type) {
            AppAccountType.PERSONAL -> {
                binding.mainBottomNavigation.inflateMenu(R.menu.bottom_nav_menu_personal)
                navController.setGraph(R.navigation.main_personal_navigation)
            }
            AppAccountType.BUSINESS -> {
                binding.mainBottomNavigation.inflateMenu(R.menu.bottom_nav_menu_enterprise)
                navController.setGraph(R.navigation.main_enterprise_navigation)
            }
            AppAccountType.ADMIN -> {
                binding.mainBottomNavigation.inflateMenu(R.menu.bottom_nav_menu_admin)
                navController.setGraph(R.navigation.main_admin_navigation)
            }
        }
        binding.mainBottomNavigation.setupWithNavController(navController)

        binding.mainBottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home, null, NavOptions.Builder()
                        .setPopUpTo(R.id.nav_home, true)
                        .build())
                    true
                }
                R.id.nav_shoplist -> {
                    navController.navigate(R.id.nav_shoplist, null, NavOptions.Builder()
                        .setPopUpTo(R.id.nav_shoplist, true)
                        .build())
                    true
                }
                R.id.nav_profile -> {
                    navController.navigate(R.id.nav_profile, null, NavOptions.Builder()
                        .setPopUpTo(R.id.nav_profile, true)
                        .build())
                    true
                }
                R.id.nav_recipe_list -> {
                    navController.navigate(R.id.nav_recipe_list, null, NavOptions.Builder()
                        .setPopUpTo(R.id.nav_recipe_list, true)
                        .build())
                    true
                }
                R.id.nav_dashboard -> {
                    navController.navigate(R.id.nav_dashboard, null, NavOptions.Builder()
                        .setPopUpTo(R.id.nav_dashboard, true)
                        .build())
                    true
                }
                else -> false
            }
        }

        schedulePantryItemValidityCheck(this)
    }

    private fun changeBottomNavigationBarVisibility(visibility: Int) {
        binding.mainBottomNavigation.visibility = visibility
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