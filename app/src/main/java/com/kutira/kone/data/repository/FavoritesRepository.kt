package com.kutira.kone.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kutira.kone.firebase.FirestorePaths
import com.kutira.kone.models.FavoriteFabric
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FavoritesRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun docId(userId: String, fabricId: String) = "${userId}_$fabricId"

    suspend fun toggleFavorite(userId: String, fabricId: String, currentlyFavorite: Boolean): Result<Unit> {
        return try {
            val ref = firestore.collection(FirestorePaths.FAVORITES).document(docId(userId, fabricId))
            if (currentlyFavorite) {
                ref.delete().await()
            } else {
                ref.set(
                    hashMapOf(
                        "userId" to userId,
                        "fabricId" to fabricId,
                        "addedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeFavorites(userId: String): Flow<Result<Set<String>>> = callbackFlow {
        val reg: ListenerRegistration = firestore.collection(FirestorePaths.FAVORITES)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val ids = snapshot?.documents?.mapNotNull {
                    it.toObject(FavoriteFabric::class.java)?.fabricId
                }?.toSet() ?: emptySet()
                trySend(Result.success(ids))
            }
        awaitClose { reg.remove() }
    }

    suspend fun isFavorite(userId: String, fabricId: String): Boolean {
        val snap = firestore.collection(FirestorePaths.FAVORITES)
            .document(docId(userId, fabricId)).get().await()
        return snap.exists()
    }
}
