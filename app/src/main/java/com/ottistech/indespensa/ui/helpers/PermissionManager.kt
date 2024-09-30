package com.ottistech.indespensa.ui.helpers

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionManager(
    private val fragment: Fragment,
    private val permissions: Array<String>,
    private val onRequestSuccess: () -> Unit,
    private val onRequestFailure: () -> Unit
) {

    fun checkPermissions(): Boolean {
        permissions.forEach { permission ->
            if(ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermissions() {
        permissionActivityResultLauncher.launch(permissions)
    }

    private val permissionActivityResultLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        permissions.forEach { (permission, granted) ->
            if(permissions.contains(permission) && !granted) {
                permissionGranted = false
                return@forEach
            }
        }
        if(!permissionGranted) {
            onRequestFailure()
        } else {
            onRequestSuccess()
        }
    }
}