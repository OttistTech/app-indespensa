package com.ottistech.indespensa.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun schedulePantryItemValidityCheck(
    context: Context
) {
    val checkValidityWork = PeriodicWorkRequestBuilder<CheckPantryItemValidityWorker>(
        1, TimeUnit.DAYS
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "CheckPantryItemValidityWork",
        ExistingPeriodicWorkPolicy.KEEP,
        checkValidityWork
    )
}