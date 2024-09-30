package com.ottistech.indespensa.ui.helpers

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.ottistech.indespensa.R

class FieldVisibilitySwitcher(
    private val context: Context
) {
    fun switch(visibility: Boolean, inputView: EditText, inputContainer: TextInputLayout) : Boolean {
        val newVisibility = !visibility
        if (newVisibility) {
            inputView.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            inputContainer.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off)
        } else {
            inputView.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            inputContainer.endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_visibility_on)
        }
        inputView.setSelection(inputView.text?.length ?: 0)
        return newVisibility
    }

}