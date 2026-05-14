package com.kutira.kone.data.repository

import com.kutira.kone.models.TradeStatus
import com.kutira.kone.utils.NotificationHelper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Local notifications for trade lifecycle events while the user is signed in.
 * Pair with Cloud Functions to deliver FCM messages when the app is backgrounded.
 */
@Singleton
class TradeNotificationCoordinator @Inject constructor(
    private val authRepository: AuthRepository,
    private val tradeRepository: TradeRepository,
    private val notificationHelper: NotificationHelper
) {
    private val notifiedIncoming = mutableSetOf<String>()

    fun start(scope: CoroutineScope) {
        scope.launch {
            authRepository.authState.collect { user ->
                coroutineContext.cancelChildren()
                notifiedIncoming.clear()
                val uid = user?.uid ?: return@collect
                launch { listenIncoming(uid) }
                launch { listenOutgoing(uid) }
            }
        }
    }

    private suspend fun listenIncoming(uid: String) {
        var ready = false
        tradeRepository.observeIncomingFor(uid)
            .distinctUntilChanged()
            .collect { result ->
                val list = result.getOrNull() ?: return@collect
                if (!ready) {
                    list.filter { it.status == TradeStatus.PENDING }.forEach { req ->
                        notifiedIncoming.add(req.id.ifBlank { req.requestId.ifBlank { req.hashCode().toString() } })
                    }
                    ready = true
                    return@collect
                }
                list.filter { it.status == TradeStatus.PENDING }.forEach { req ->
                    val key = req.id.ifBlank { req.requestId.ifBlank { req.hashCode().toString() } }
                    if (notifiedIncoming.add(key)) {
                        notificationHelper.showTradeNotification(
                            title = "New trade request",
                            message = "Someone requested a swap for one of your fabrics."
                        )
                    }
                }
            }
    }

    private suspend fun listenOutgoing(uid: String) {
        var ready = false
        tradeRepository.observeOutgoingFor(uid)
            .distinctUntilChanged()
            .collect { result ->
                val list = result.getOrNull() ?: return@collect
                if (!ready) {
                    list.filter { it.status == TradeStatus.ACCEPTED }.forEach { req ->
                        notifiedIncoming.add("out_${req.id.ifBlank { req.requestId }}")
                    }
                    ready = true
                    return@collect
                }
                list.filter { it.status == TradeStatus.ACCEPTED }.forEach { req ->
                    val key = "out_${req.id.ifBlank { req.requestId }}"
                    if (notifiedIncoming.add(key)) {
                        notificationHelper.showTradeNotification(
                            title = "Trade accepted",
                            message = "A vendor accepted your trade request."
                        )
                    }
                }
            }
    }
}
