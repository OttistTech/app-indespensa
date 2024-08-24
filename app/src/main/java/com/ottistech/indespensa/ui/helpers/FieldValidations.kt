package com.ottistech.indespensa.ui.helpers

import android.content.Context
import android.util.Patterns
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.ottistech.indespensa.R

class FieldValidations(
    val context: Context
) {

    fun validNotNull(inputView: TextView, inputContainer : TextInputLayout, errorMessageView: TextView) : Boolean {
        return if(inputView.text.isNullOrBlank()) {
            setFieldError(inputContainer, errorMessageView, context.getString(R.string.form_error_not_null, inputView.hint))
            false
        } else {
            removeFieldError(inputContainer, errorMessageView)
            true
        }
    }

    fun validIsEmail(inputView: TextView, inputContainer : TextInputLayout, errorMessageView: TextView) : Boolean {
        return if(!Patterns.EMAIL_ADDRESS.matcher(inputView.text.toString()).matches()) {
            setFieldError(inputContainer, errorMessageView, context.getString(R.string.form_error_not_email, inputView.hint))
            false
        } else {
            removeFieldError(inputContainer, errorMessageView)
            true
        }
    }

    fun validPassword(inputView: TextView, inputContainer: TextInputLayout, errorMessageView: TextView) : Boolean {
        return if(inputView.text?.length!! < 8) {
            setFieldError(inputContainer, errorMessageView, context.getString(R.string.form_error_password, "ter no mínimo 8 caracteres"))
            false
        } else {
            removeFieldError(inputContainer, errorMessageView)
            true
        }
    }

    fun validConfirmation(inputView: TextView, inputContainer : TextInputLayout, errorMessageView: TextView, matchWord: String?) : Boolean {
         if(!matchWord.isNullOrBlank() || !inputView.text.isNullOrBlank()) {
             return if(inputView.text.toString() != matchWord) {
                setFieldError(inputContainer, errorMessageView, context.getString(R.string.form_error_repeat_password))
                false
            } else {
                removeFieldError(inputContainer, errorMessageView)
                true
            }
        }
        return false
    }

    fun validCep(inputView: TextView, inputContainer: TextInputLayout, errorMessageView: TextView) : Boolean {
        return if(inputView.text?.length!! != 8) {
            setFieldError(inputContainer, errorMessageView, context.getString(R.string.form_error_not_cep, inputView.hint))
            false
        } else {
            removeFieldError(inputContainer, errorMessageView)
            true
        }
    }

    fun validMaxLength(inputView: TextView, inputContainer: TextInputLayout, errorMessageView: TextView, maxLength: Int) : Boolean {
        return if(inputView.text?.length!! > maxLength) {
            setFieldError(inputContainer, errorMessageView, context.getString(R.string.form_error_max_length, inputView.hint, maxLength))
            false
        } else {
            removeFieldError(inputContainer, errorMessageView)
            true
        }
    }

    fun validMinLength(inputView: TextView, inputContainer: TextInputLayout, errorMessageView: TextView, minLength: Int) : Boolean {
        return if(inputView.text?.length!! < minLength) {
            setFieldError(inputContainer, errorMessageView, context.getString(R.string.form_error_min_length, inputView.hint, minLength))
            false
        } else {
            removeFieldError(inputContainer, errorMessageView)
            true
        }
    }

    fun validCheckBox(checkBox: CheckBox, checkBoxText: TextView) : Boolean {
        return if(!checkBox.isChecked) {
            checkBox.error = context.getString(R.string.form_error_checkbox)
            checkBoxText.setTextColor(context.getColor(R.color.red))
            false
        } else {
            checkBoxText.error = null
            checkBoxText.setTextColor(context.getColor(R.color.black))
            true
        }
    }

    fun setFieldError(inputContainer: TextInputLayout?, errorMessageView: TextView, error: String) {
        errorMessageView.text = error
        errorMessageView.visibility = View.VISIBLE
        inputContainer?.error = error
    }

    fun removeFieldError(inputContainer: TextInputLayout?, errorMessageView: TextView) {
        errorMessageView.visibility = View.GONE
        inputContainer?.error = null
    }

}