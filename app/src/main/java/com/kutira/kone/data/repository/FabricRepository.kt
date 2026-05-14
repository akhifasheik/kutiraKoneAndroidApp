package com.kutira.kone.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.kutira.kone.firebase.FirestorePaths
import com.kutira.kone.models.Fabric
import com.kutira.kone.utils.ImageUtils
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class FabricRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val imageUtils: ImageUtils
) {
    fun observeFabricsRealtime(): Flow<Result<List<Fabric>>> = callbackFlow {
        var registration: ListenerRegistration? = null
        try {
            registration = firestore.collection(FirestorePaths.FABRICS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }
                    val items = snapshot?.documents?.mapNotNull { it.toObject(Fabric::class.java) }
                        ?: emptyList()
                    trySend(Result.success(items))
                }
        } catch (e: Exception) {
            trySend(Result.failure(e))
        }
        awaitClose { registration?.remove() }
    }

    suspend fun uploadFabricImage(userId: String, imageUri: Uri): String {
        val bytes = imageUtils.compressToJpeg(imageUri)
        val name = "${UUID.randomUUID()}.jpg"
        val ref = storage.reference.child("fabric_images/$userId/$name")
        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun createFabric(fabric: Fabric): Result<Unit> {
        return try {
            val doc = firestore.collection(FirestorePaths.FABRICS).document()
            val withId = fabric.copy(id = doc.id)
            doc.set(withId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setAvailability(fabricId: String, available: Boolean): Result<Unit> {
        return try {
            firestore.collection(FirestorePaths.FABRICS).document(fabricId)
                .update("available", available).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFabric(fabricId: String, sellerId: String): Result<Unit> {
        return try {
            val doc = firestore.collection(FirestorePaths.FABRICS).document(fabricId).get().await()
            val fabric = doc.toObject(Fabric::class.java) ?: return Result.failure(
                IllegalStateException("Fabric not found")
            )
            if (fabric.sellerId != sellerId) {
                return Result.failure(SecurityException("Not owner"))
            }
            if (fabric.imageUrl.isNotBlank()) {
                try {
                    FirebaseStorage.getInstance().getReferenceFromUrl(fabric.imageUrl).delete().await()
                } catch (_: Exception) {
                    /* ignore storage delete failures */
                }
            }
            doc.reference.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFabric(fabricId: String): Fabric? {
        val snap = firestore.collection(FirestorePaths.FABRICS).document(fabricId).get().await()
        return snap.toObject(Fabric::class.java)
    }

    fun observeSellerFabrics(sellerId: String): Flow<Result<List<Fabric>>> = callbackFlow {
        val reg = firestore.collection(FirestorePaths.FABRICS)
            .whereEqualTo("sellerId", sellerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toObject(Fabric::class.java) } ?: emptyList()
                trySend(Result.success(list))
            }
        awaitClose { reg.remove() }
    }
}
