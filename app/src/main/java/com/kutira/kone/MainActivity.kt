package com.kutira.kone

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kutira.kone.data.repository.TradeNotificationCoordinator
import com.kutira.kone.ui.navigation.KutiraNavHost
import com.kutira.kone.ui.theme.KutiraKoneTheme
import com.kutira.kone.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import android.widget.Toast
import com.razorpay.PaymentResultListener

@AndroidEntryPoint
class MainActivity :
    ComponentActivity(),
    PaymentResultListener {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var tradeNotificationCoordinator: TradeNotificationCoordinator

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        notificationHelper.ensureChannels()
        tradeNotificationCoordinator.start(lifecycleScope)

        setContent {
            KutiraKoneTheme {
                var notificationGranted by remember {
                    mutableStateOf(
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionState = rememberMultiplePermissionsState(
                        permissions = listOf(Manifest.permission.POST_NOTIFICATIONS)
                    )
                    LaunchedEffect(Unit) {
                        if (!permissionState.allPermissionsGranted) {
                            permissionState.launchMultiplePermissionRequest()
                        }
                        notificationGranted = permissionState.permissions.all { it.status.isGranted }
                    }
                }

                KutiraNavHost()
            }
        }
    }
    override fun onPaymentSuccess(
        razorpayPaymentId: String?
    ) {

        Toast.makeText(
            this,
            "Payment Successful!",
            Toast.LENGTH_LONG
        ).show()

        finish()

        startActivity(intent)
    }

    override fun onPaymentError(
        code: Int,
        response: String?
    ) {

        Toast.makeText(
            this,
            "Payment Failed!",
            Toast.LENGTH_LONG
        ).show()
    }
}
