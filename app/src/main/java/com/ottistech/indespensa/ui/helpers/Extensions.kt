package com.ottistech.indespensa.ui.helpers

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import coil.load
import com.ottistech.indespensa.R
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun String.toDate(format: String = "dd/MM/yyyy"): Date? {
    val simpleDateFormat = SimpleDateFormat(format)
    return try {
        simpleDateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun ImageView.loadImage(url: String?) {
    val alternativeImage = R.drawable.placeholder_image
    load(url) {
        placeholder(alternativeImage)
        fallback(alternativeImage)
        error(alternativeImage)
    }
}