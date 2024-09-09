package com.ottistech.indespensa.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.DialogConfirmationBinding

class ConfirmationDialogCreator(
    private val context: Context
) {

    fun showDialog(
        message: String,
        actionText: String,
        onConfirm: () -> Unit
    ) {
        val binding = DialogConfirmationBinding.inflate(LayoutInflater.from(context))
        binding.dialogConfirmationMessage.text = message
        binding.dialogConfirmationConfirm.text = actionText

        val dialog = AlertDialog.Builder(context, R.style.IndespensaConfirmationDialog)
            .setView(binding.root)
            .create()

        binding.dialogConfirmationConfirm.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
        binding.dialogConfirmationCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}