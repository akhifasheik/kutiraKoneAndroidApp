package com.kutira.kone.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client by lazy { LocationServices.getFusedLocationProviderClient(context) }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationOrNull(): Location? {
        return try {
            var loc = client.lastLocation.await()
            if (loc == null) {
                val cts = CancellationTokenSource()
                loc = client.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cts.token
                ).await()
            }
            loc
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getCurrentLocationAwait(): Location {
        return getCurrentLocationOrNull()
            ?: throw IllegalStateException("Unable to obtain location. Enable GPS and grant permission.")
    }
}
