package com.kutira.kone.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kutira.kone.firebase.FirestorePaths
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun upsertProfile(uid: String, phone: String?, role: String?) {
        val data = hashMapOf<String, Any>(
            "uid" to uid,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        phone?.let { data["phone"] = it }
        role?.let { data["role"] = it }
        firestore.collection(FirestorePaths.USERS).document(uid).set(data, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    suspend fun updateFcmToken(uid: String, token: String?) {
        if (token.isNullOrBlank()) return
        firestore.collection(FirestorePaths.USERS).document(uid)
            .set(
                hashMapOf(
                    "fcmToken" to token,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()
    }
}
