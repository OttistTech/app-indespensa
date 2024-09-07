package com.ottistech.indespensa.data.datasource

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ImageFirebaseDatasource(
    private val path: String,
) {

    private val TAG = "IMAGE FIREBASE DATASOURCE"
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImage(photo: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val byteData = byteArrayOutputStream.toByteArray()

        return suspendCoroutine { continuation ->
            Log.d(TAG, "[uploadImage] Trying to upload an image to $path")
            storage.getReference(path).child(
                "photo ${System.currentTimeMillis()}.jpg"
            ).putBytes(byteData).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { imageUri ->
                    Log.d(TAG, "[uploadImage] Uploaded image successfully")
                    val imageUriString = imageUri.toString()
                    continuation.resume(imageUriString)
                }?.addOnFailureListener { e ->
                    Log.e(TAG, "[uploadImage] Failed while uploading image", e)
                    continuation.resumeWithException(e)
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "[uploadImage] Failed while uploading image", e)
                continuation.resumeWithException(e)
            }
        }
    }
}