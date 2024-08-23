package com.ottistech.indespensa.ui.helpers

import android.annotation.SuppressLint
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