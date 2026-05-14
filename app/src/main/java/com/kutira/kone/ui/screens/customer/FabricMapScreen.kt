package com.kutira.kone.ui.screens.customer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.kutira.kone.ui.viewmodel.MarketplaceViewModel
import com.kutira.kone.utils.DistanceUtils

@Composable
fun FabricMapScreen(
    viewModel: MarketplaceViewModel,
    onOpenDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.items.collectAsState()
    val userLatLng by viewModel.userLatLng.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        val anchor = userLatLng?.let { LatLng(it.first, it.second) }
            ?: items.firstOrNull()?.let { LatLng(it.fabric.latitude, it.fabric.longitude) }
            ?: LatLng(12.97, 77.59)
        position = CameraPosition.fromLatLngZoom(anchor, 12f)
    }

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false)
    }

    Box(modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = uiSettings
        ) {
            items.forEach { row ->
                val pos = LatLng(row.fabric.latitude, row.fabric.longitude)
                val snippet = row.distanceKm?.let { DistanceUtils.formatDistance(it) }.orEmpty()
                Marker(
                    state = MarkerState(position = pos),
                    title = row.fabric.materialType,
                    snippet = snippet,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                    onClick = {
                        onOpenDetails(row.fabric.id)
                        true
                    }
                )
            }
        }
        Text(
            "Pins show filtered fabrics within your radius.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp)
        )
    }
}
