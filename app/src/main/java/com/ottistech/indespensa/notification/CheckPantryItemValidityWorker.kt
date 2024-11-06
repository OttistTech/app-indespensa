package com.ottistech.indespensa.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ottistech.indespensa.R
import com.ottistech.indespensa.data.repository.PantryRepository
import com.ottistech.indespensa.shared.ProductItemType
import com.ottistech.indespensa.ui.activity.MainActivity
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class CheckPantryItemValidityWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val TAG = "PANTRY ITEM VALIDITY DATE NOTIFICATION WORKER"

    override suspend fun doWork(): Result {
        return try {
            val pantryRepository = PantryRepository(applicationContext)

            val ingredients = withContext(Dispatchers.IO) {
                pantryRepository.listCloseValidityItems()
            }

            createNotificationChannel()

            ingredients.forEach { ingredient ->
                sendNotification(ingredient)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun sendNotification(ingredient: PantryItemCloseValidityDTO) {

        try {
            val expirationDate = ingredient.validityDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val today = LocalDate.now()

            val daysUntilExpiration = ChronoUnit.DAYS.between(today, expirationDate)

            val notificationText = if (daysUntilExpiration > 0) {
                "${ingredient.productName} vence em $daysUntilExpiration dias."
            } else {
                "${ingredient.productName} já passou da data de validade!"
            }

            val notificationTitle = if (daysUntilExpiration > 0) {
                "Produto próximo da data de validade!"
            } else {
                "Produto Expirado: Hora de Descartar"
            }

            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("navigateTo", R.id.product_details_dest)
                    putExtra("pantryItemId", ingredient.pantryItemId)
                    putExtra("itemType", ProductItemType.PANTRY_ITEM)
                }

                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    ingredient.pantryItemId.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(applicationContext, NotificationConstants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()

                NotificationManagerCompat.from(applicationContext).notify(ingredient.pantryItemId.hashCode(), notification)
                Log.d(TAG, "Notification sent successfully")
            } else {
                Log.e(TAG, "Permission of sending notification denied")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing date or sending notification", e)

        }

    }

    private fun createNotificationChannel() {
        val name = "Ingredient Expiration Alerts"
        val descriptionText = "Notifies when ingredients are close to their expiration date"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NotificationConstants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}