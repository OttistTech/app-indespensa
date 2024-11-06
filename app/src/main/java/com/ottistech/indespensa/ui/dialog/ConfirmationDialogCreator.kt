package com.ottistech.indespensa.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.DialogConfirmationBinding
import com.ottistech.indespensa.shared.loadImage

class ConfirmationDialogCreator(
    private val context: Context
) {

    fun showDialog(
        title: String? = null,
        description: CharSequence? = null,
        imageUrl: String? = null,
        actionText: String? = null,
        onConfirm: () -> Unit
    ) {
        val binding = DialogConfirmationBinding.inflate(LayoutInflater.from(context))
        title?.let {
            binding.dialogConfirmationTitle.visibility = View.VISIBLE
            binding.dialogConfirmationTitle.text = it
        }
        description?.let {
            binding.dialogConfirmationDescription.visibility = View.VISIBLE
            binding.dialogConfirmationDescription.text = it
        }
        imageUrl?.let {
            binding.dialogConfirmationImage.visibility = View.VISIBLE
            binding.dialogConfirmationImage.loadImage(it)
        }
        binding.dialogConfirmationConfirm.text = actionText ?: context.getString(R.string.cta_confirm)

        val dialog = AlertDialog.Builder(context, R.style.IndespensaDialog)
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