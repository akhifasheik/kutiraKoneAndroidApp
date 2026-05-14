package com.kutira.kone.ui.screens.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kutira.kone.models.Fabric
import com.kutira.kone.ui.components.EmptyState
import com.kutira.kone.ui.components.NetworkErrorState
import com.kutira.kone.ui.viewmodel.VendorDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    onUploadClick: () -> Unit,
    viewModel: VendorDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor studio", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onUploadClick) {
                Icon(Icons.Outlined.Add, contentDescription = "Upload fabric")
            }
        }
    ) { padding ->
        when {
            state.error != null -> NetworkErrorState(
                message = state.error ?: "Unable to load listings",
                onRetry = { /* Flow auto updates */ },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )

            state.fabrics.isEmpty() -> EmptyState(
                title = "No scraps yet",
                subtitle = "Capture a photo, add details, and publish your first zero-waste listing.",
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                actionLabel = "Upload fabric",
                onAction = onUploadClick
            )

            else -> LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Your listings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Toggle availability when a scrap is reserved or gone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(state.fabrics, key = { it.id }) { fabric ->
                    VendorFabricRow(fabric = fabric, onToggle = { viewModel.toggleAvailability(it) })
                }
            }
        }
    }
}

@Composable
private fun VendorFabricRow(
    fabric: Fabric,
    onToggle: (Fabric) -> Unit
) {
    Card(
        shape = CardDefaults.shape,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(
                model = fabric.imageUrl,
                contentDescription = fabric.materialType,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Text(fabric.materialType, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "${fabric.color} · ${fabric.size}",
                style = MaterialTheme.typography.bodySmall
            )
            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (fabric.available) "Available" else "Hidden",
                    style = MaterialTheme.typography.labelLarge
                )
                Switch(
                    checked = fabric.available,
                    onCheckedChange = { onToggle(fabric) }
                )
            }
        }
    }
}
