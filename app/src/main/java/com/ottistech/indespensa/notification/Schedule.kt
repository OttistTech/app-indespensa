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
        15, TimeUnit.MINUTES
    ).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        NotificationConstants.WORKER_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        checkValidityWork
    )
}