package com.ottistech.indespensa.ui.helpers

import android.widget.EditText
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DatePickerCreator {
    fun createDatePicker(resultView: EditText, label: String): MaterialDatePicker<Long> {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(label)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val timeZoneUTC = TimeZone.getDefault()
            val offsetFromUTC = timeZoneUTC.getOffset(Date(it).time) * -1
            val simpleFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(it + offsetFromUTC)
            val formattedDate = simpleFormat.format(date)
            resultView.setText(formattedDate)
        }

        return datePicker
    }
}