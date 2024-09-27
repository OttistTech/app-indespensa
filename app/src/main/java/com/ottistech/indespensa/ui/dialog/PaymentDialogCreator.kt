package com.ottistech.indespensa.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ottistech.indespensa.R
import com.ottistech.indespensa.databinding.DialogPaymentBinding
import com.ottistech.indespensa.ui.helpers.makeSpanText

class PaymentDialogCreator(
    private val context: Context
) {

    fun showPaymentDialog(
        paymentAmount: String,
        onConfirm: () -> Unit
    ) {
        val binding = DialogPaymentBinding.inflate(LayoutInflater.from(context))

        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .create()

        binding.paymentConfirmPaymentButton.setOnClickListener {
            onConfirm()

            // Open new window showing that user is now premium
             dialog.dismiss()
        }

        binding.premiumNormalText.text = makeSpanText(
            context.getString(R.string.payment_text, paymentAmount),
            paymentAmount,
            ContextCompat.getColor(context, R.color.secondary)
        )

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        val width = (context.resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
