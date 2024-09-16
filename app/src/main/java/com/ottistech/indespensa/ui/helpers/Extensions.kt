package com.ottistech.indespensa.ui.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.webclient.dto.UserCredentialsDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("SimpleDateFormat")
fun String.toDate(format: String = "dd/MM/yyyy"): Date? {
    val simpleDateFormat = SimpleDateFormat(format)
    return try {
        simpleDateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.formatAsString(format: String = "dd/MM/yyyy") : String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    return simpleDateFormat.format(this)
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

fun Context.getCurrentUser() : UserCredentialsDTO {
    return UserRepository(this).getUserCredentials()
}

fun ImageView.tryLoadImage(url: String? = null) {
    val defaultImage = R.drawable.imagem_padrao
    load(url) {
        placeholder(defaultImage)
        fallback(defaultImage)
        error(defaultImage)
    }
}

