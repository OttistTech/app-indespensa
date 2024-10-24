package com.ottistech.indespensa.ui.helpers

import android.util.Patterns

fun validNotNull(value: String?) : Boolean {
    return !value.isNullOrBlank()
}

fun validIsEmail(value: String?) : Boolean {
    return value?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } ?: false
}

fun validPassword(value: String?) : Boolean {
    return value?.length!! >= 8
}

fun validConfirmation(value: String?, matchWord: String?) : Boolean {
    if(!matchWord.isNullOrBlank() || !value.isNullOrBlank()) {
        return value.toString() == matchWord
    }
    return false
}

fun validCep(value: String?) : Boolean {
    return value?.length!! == 8
}

fun validMaxLength(value: String?, maxLength: Int) : Boolean {
    return value?.length!! <= maxLength
}

fun validMinLength(value: String?, minLength: Int) : Boolean {
    return value?.length!! >= minLength
}

fun validMaxValue(value: String?, maxValue: Int) : Boolean {
    return if(validNotNull(value)) {
        value.toString().toInt() <= maxValue
    } else {
        false
    }
}

fun validMinValue(value: String?, minValue: Int) : Boolean {
    return if(validNotNull(value)) {
        value.toString().toInt() >= minValue
    } else {
        false
    }
}