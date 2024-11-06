package com.ottistech.indespensa.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ottistech.indespensa.databinding.DialogRatingBinding

class RatingDialogCreator(
    private val context: Context
) {

    fun showRatingDialog(
        onConfirm: (Int) -> Unit
    ) {
        val binding = DialogRatingBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        var userRating = 0

        binding.dialogRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            userRating = rating.toInt()
        }

        binding.dialogRatingButtonRating.setOnClickListener {
            onConfirm(userRating)

            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        val width = (context.resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
