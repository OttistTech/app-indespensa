package com.ottistech.indespensa.ui.helpers

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun clearInputFocus(
    event: MotionEvent,
    currentFocus: View?,
    inputMethodManager: InputMethodManager
) {
    if (event.action == MotionEvent.ACTION_DOWN) {
        if (currentFocus is EditText) {
            val outRect = Rect()
            currentFocus.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                currentFocus.clearFocus()
                inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
            }
        }
    }
}