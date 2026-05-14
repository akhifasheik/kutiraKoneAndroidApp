@file:OptIn(ExperimentalLayoutApi::class)

package com.kutira.kone.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kutira.kone.ui.components.EmptyState
import com.kutira.kone.ui.components.FabricGridCard
import com.kutira.kone.ui.components.NetworkErrorState
import com.kutira.kone.ui.components.ShimmerFabricGrid
import com.kutira.kone.ui.viewmodel.MarketplaceViewModel
import com.kutira.kone.utils.DistanceUtils

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel,
    onOpenDetails: (String) -> Unit,
    favoriteIds: Set<String>,
    onToggleFavorite: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.items.collectAsState()
    val query by viewModel.query.collectAsState()
    val material by viewModel.material.collectAsState()
    val radius by viewModel.radiusKm.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    val locationError by viewModel.locationError.collectAsState()
    val refreshing by viewModel.refreshing.collectAsState()

    val pullState = rememberPullRefreshState(refreshing, onRefresh = { viewModel.refreshFabrics() })

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Text(
            "Nearby marketplace",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        locationError?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::setQuery,
            label = { Text("Search fabrics") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Text("Material", style = MaterialTheme.typography.labelLarge)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            viewModel.materials.forEach { m ->
                FilterChip(
                    selected = material == m,
                    onClick = { viewModel.setMaterial(m) },
                    label = { Text(m) }
                )
            }
        }
        Text("Radius", style = MaterialTheme.typography.labelLarge)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            listOf(1.0, 3.0, 5.0, 10.0).forEach { km ->
                FilterChip(
                    selected = radius == km,
                    onClick = { viewModel.setRadiusKm(km) },
                    label = { Text("${km.toInt()} km") }
                )
            }
        }
        Spacer(Modifier.height(4.dp))

        when {
            loadError != null -> NetworkErrorState(
                message = loadError ?: "Network error",
                onRetry = { viewModel.refreshFabrics() },
                modifier = Modifier.fillMaxSize()
            )

            items.isEmpty() -> EmptyState(
                title = "No fabrics in this radius",
                subtitle = "Try widening the radius or changing filters. New listings appear instantly.",
                modifier = Modifier.fillMaxSize(),
                actionLabel = "Refresh",
                onAction = { viewModel.refreshFabrics() }
            )

            else -> Box(
                Modifier
                    .fillMaxSize()
                    .pullRefresh(pullState)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 96.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items, key = { it.fabric.id }) { row ->
                        val dist = row.distanceKm?.let { DistanceUtils.formatDistance(it) }
                        FabricGridCard(
                            fabric = row.fabric,
                            distanceLabel = dist,
                            onClick = { onOpenDetails(row.fabric.id) },
                            favorite = favoriteIds.contains(row.fabric.id),
                            onFavoriteToggle = {
                                onToggleFavorite(row.fabric.id, favoriteIds.contains(row.fabric.id))
                            }
                        )
                    }
                }
                PullRefreshIndicator(
                    refreshing = refreshing,
                    state = pullState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
