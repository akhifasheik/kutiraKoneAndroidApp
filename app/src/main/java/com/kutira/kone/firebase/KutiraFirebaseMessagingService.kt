package com.kutira.kone.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kutira.kone.data.repository.UserRepository
import com.kutira.kone.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class KutiraFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        scope.launch {
            runCatching { userRepository.updateFcmToken(uid, token) }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"].orEmpty()
        val title = message.notification?.title ?: message.data["title"].orEmpty().ifBlank { "Kutira-Kone" }
        val body = message.notification?.body ?: message.data["body"].orEmpty()
        when (type) {
            "trade_request", "trade_received" -> {
                notificationHelper.showTradeNotification(
                    title = title.ifBlank { "New trade request" },
                    message = body.ifBlank { "You have a new fabric trade request." }
                )
            }
            "trade_accepted" -> {
                notificationHelper.showTradeNotification(
                    title = title.ifBlank { "Trade accepted" },
                    message = body.ifBlank { "Your trade request was accepted." }
                )
            }
            "nearby_fabric" -> {
                notificationHelper.showNearbyFabricNotification(
                    title = title.ifBlank { "New fabric nearby" },
                    message = body.ifBlank { "A new scrap listing appeared near you." }
                )
            }
            else -> {
                if (body.isNotBlank()) {
                    notificationHelper.showTradeNotification(title = title, message = body)
                }
            }
        }
    }
}
