package com.ottistech.indespensa.ui.helpers

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

fun makeSpanText(
    fullText: String,
    spanExcerpt: String,
    spanColor: Int
) : SpannableString {
    val spanStart = fullText.indexOf(spanExcerpt)
    val spanEnd = spanStart + spanExcerpt.length

    val spannable = SpannableString(fullText)
    spannable.setSpan(
        ForegroundColorSpan(spanColor),
        spanStart,
        spanEnd,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
}