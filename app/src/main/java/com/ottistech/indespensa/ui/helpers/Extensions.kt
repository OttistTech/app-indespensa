package com.ottistech.indespensa.ui.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.UserRepository
import com.ottistech.indespensa.webclient.dto.user.UserCredentialsDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


@SuppressLint("SimpleDateFormat")
// Function that converts a String into a Date with specified format
fun String.toDate(format: String = "dd/MM/yyyy"): Date? {
    val simpleDateFormat = SimpleDateFormat(format)
    return try {
        simpleDateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

// Function that converts a Date into a String with specified format
fun Date.formatAsString(format: String = "dd/MM/yyyy") : String {
    val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
    return simpleDateFormat.format(this)
}

// Function that simplifies to show a toast
fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

// Function that loads content into a ImageView
fun ImageView.loadImage(url: String?) {
    val alternativeImage = R.drawable.alternative_image
    load(url) {
        crossfade(true)
        placeholder(alternativeImage)
        fallback(alternativeImage)
        error(alternativeImage)
    }
}

// Overloaded function to load a drawable resource
fun ImageView.loadImage(resId: Int) {
    load(resId) {
        placeholder(R.drawable.alternative_image)
        error(R.drawable.alternative_image)
    }
}

// Function used to obtain the current user credentials from anywhere in the Application
fun Context.getCurrentUser() : UserCredentialsDTO {
    return UserRepository(this).getUserCredentials()
}

// Function to render a validity date in its respective color into a TextView
fun TextView.renderValidityDate(validityDate: Date) {
    val currentDate = Date()
    val daysLeft = TimeUnit.MILLISECONDS.toDays(validityDate.time - currentDate.time)
    if(daysLeft > 10) {
        this.setTextColor(ContextCompat.getColor(context, R.color.green))
    } else {
        this.setTextColor(ContextCompat.getColor(context, R.color.red))
    }
    this.text = validityDate.formatAsString()
}

@SuppressLint("SetTextI18n")
// Function to render a amount into a TextView
fun TextView.renderAmount(amount: Number, unit: String) {
    this.text = amount.toString() + unit
}
