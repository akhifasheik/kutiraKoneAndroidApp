package com.kutira.kone.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.kutira.kone.firebase.FirestorePaths
import com.kutira.kone.models.TradeRequest
import com.kutira.kone.models.TradeStatus
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.kutira.kone.models.ShippedProduct
import com.google.firebase.firestore.Query

@Singleton
class TradeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun createTradeRequest(
        senderId: String,
        senderPhone: String,
        receiverId: String,
        fabricId: String,
        fabricTitle: String,

        offeredItem: String,
        offeredDescription: String,
        offeredSize: String,
        offeredColor: String,

        tradeMessage: String
    ): Result<String> {

        return try {

            // CHECK FOR EXISTING PENDING REQUEST
            val existing = firestore.collection(FirestorePaths.TRADE_REQUESTS)
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", receiverId)
                .whereEqualTo("fabricId", fabricId)
                .whereEqualTo("status", TradeStatus.PENDING)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.failure(
                    IllegalStateException("Trade request already sent")
                )
            }

            val doc = firestore.collection(FirestorePaths.TRADE_REQUESTS).document()

            val request = TradeRequest(
                id = doc.id,
                requestId = doc.id,
                senderId = senderId,
                senderPhone = senderPhone,
                receiverId = receiverId,
                fabricId = fabricId,
                fabricTitle = fabricTitle,
                offeredItem = offeredItem,
                offeredDescription = offeredDescription,
                offeredSize = offeredSize,
                offeredColor = offeredColor,
                tradeMessage = tradeMessage,
                status = TradeStatus.PENDING
            )

            doc.set(request).await()

            Result.success(doc.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeIncomingFor(
        receiverId: String
    ): Flow<Result<List<TradeRequest>>> = callbackFlow {

        val reg: ListenerRegistration =
            firestore.collection(FirestorePaths.TRADE_REQUESTS)
                .whereEqualTo("receiverId", receiverId)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }

                    val list = snapshot?.documents
                        ?.mapNotNull {
                            it.toObject(TradeRequest::class.java)
                        }
                        ?: emptyList()

                    trySend(Result.success(list))
                }

        awaitClose {
            reg.remove()
        }
    }

    fun observeOutgoingFor(
        senderId: String
    ): Flow<Result<List<TradeRequest>>> = callbackFlow {

        val reg =
            firestore.collection(FirestorePaths.TRADE_REQUESTS)
                .whereEqualTo("senderId", senderId)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        trySend(Result.failure(error))
                        return@addSnapshotListener
                    }

                    val list = snapshot?.documents
                        ?.mapNotNull {
                            it.toObject(TradeRequest::class.java)
                        }
                        ?: emptyList()

                    trySend(Result.success(list))
                }

        awaitClose {
            reg.remove()
        }
    }

    suspend fun updateStatus(
        requestId: String,
        receiverId: String,
        status: String
    ): Result<Unit> {

        return try {

            val ref =
                firestore.collection(FirestorePaths.TRADE_REQUESTS)
                    .document(requestId)

            val snap = ref.get().await()

            val existing =
                snap.toObject(TradeRequest::class.java)
                    ?: return Result.failure(
                        IllegalStateException("Missing request")
                    )

            if (existing.receiverId != receiverId) {
                return Result.failure(
                    SecurityException("Not authorized")
                )
            }

            val updateData = mutableMapOf<String, Any>(
                "status" to status,
                "timestamp" to FieldValue.serverTimestamp()
            )

            // GENERATE ORDER ID WHEN ACCEPTED
            if (status == TradeStatus.ACCEPTED) {

                updateData["orderId"] =
                    "TRD-${System.currentTimeMillis()}"

                // REMOVE FABRIC FROM MARKETPLACE
                firestore.collection(FirestorePaths.FABRICS)
                    .document(existing.fabricId)
                    .update(
                        mapOf(
                            "available" to false
                        )
                    )
                    .await()
            }

            ref.update(updateData).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun pendingCountForReceiver(
        receiverId: String
    ): Flow<Result<Int>> = callbackFlow {

        val reg =
            firestore.collection(FirestorePaths.TRADE_REQUESTS)
                .whereEqualTo("receiverId", receiverId)
                .whereEqualTo("status", TradeStatus.PENDING)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        trySend(Result.failure(error))
                    } else {
                        trySend(Result.success(snapshot?.size() ?: 0))
                    }
                }

        awaitClose {
            reg.remove()
        }
    }

    // ACCEPTED TRADES
    fun observeAcceptedTrades(
        userId: String
    ): Flow<Result<List<TradeRequest>>> = callbackFlow {

        val reg =
            firestore.collection(FirestorePaths.TRADE_REQUESTS)
                .whereEqualTo("senderId", userId)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {

                        trySend(Result.failure(error))

                        return@addSnapshotListener
                    }

                    val list = snapshot?.documents
                        ?.mapNotNull {

                            it.toObject(
                                TradeRequest::class.java
                            )
                        }
                        ?.filter {

                            it.status == TradeStatus.ACCEPTED ||

                                    it.status ==
                                    TradeStatus.CUSTOMER_SHIPPED ||

                                    it.status ==
                                    TradeStatus.VENDOR_SHIPPED ||

                                    it.status ==
                                    TradeStatus.COMPLETED
                        }
                        ?.sortedByDescending {
                            it.timestamp
                        }

                        ?: emptyList()

                    trySend(Result.success(list))
                }

        awaitClose {
            reg.remove()
        }
    }

    suspend fun updateShipmentStatus(
        requestId: String,
        status: String
    ): Result<Unit> {

        return try {

            firestore.collection(FirestorePaths.TRADE_REQUESTS)
                .document(requestId)
                .update(
                    mapOf(
                        "status" to status
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
    suspend fun markVendorShipped(
        trade: TradeRequest
    ): Result<Unit> {

        return try {

            // UPDATE TRADE STATUS
            firestore.collection(
                FirestorePaths.TRADE_REQUESTS
            )
                .document(trade.id)
                .update(
                    "status",
                    TradeStatus.VENDOR_SHIPPED
                )
                .await()

            // CREATE SHIPMENT ID
            val shipmentId =
                firestore.collection(
                    "shipped_products"
                ).document().id

            // CREATE SHIPPED PRODUCT
            val shippedProduct = ShippedProduct(

                shipmentId = shipmentId,

                tradeId = trade.id,

                vendorId = trade.receiverId,

                customerId = trade.senderId,

                fabricId = trade.fabricId,

                fabricTitle = trade.fabricTitle,

                customerPhone = trade.senderPhone,

                offeredItem = trade.offeredItem,

                offeredSize = trade.offeredSize,

                offeredColor = trade.offeredColor,

                status = TradeStatus.VENDOR_SHIPPED
            )

            // SAVE TO FIRESTORE
            firestore.collection(
                "shipped_products"
            )
                .document(shipmentId)
                .set(shippedProduct)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    companion object {
        fun newRequestId(): String = UUID.randomUUID().toString()
    }
}