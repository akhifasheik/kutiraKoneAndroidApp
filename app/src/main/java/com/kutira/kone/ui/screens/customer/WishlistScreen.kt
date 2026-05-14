package com.kutira.kone.ui.screens.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kutira.kone.ui.components.EmptyState
import com.kutira.kone.ui.components.FabricGridCard
import com.kutira.kone.ui.viewmodel.CustomerShellViewModel
import com.kutira.kone.ui.viewmodel.WishlistViewModel

@Composable
fun WishlistScreen(
    onOpenDetails: (String) -> Unit,
    shellViewModel: CustomerShellViewModel,
    wishlistViewModel: WishlistViewModel = hiltViewModel()
) {
    val favorites by wishlistViewModel.favoriteFabrics.collectAsState()
    val favoriteIds by shellViewModel.favoriteIds.collectAsState()

    if (favorites.isEmpty()) {
        EmptyState(
            title = "Wishlist is empty",
            subtitle = "Tap the heart on any fabric card to save it for later.",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(2) }) {
            Text(
                "Saved fabrics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        items(favorites, key = { it.id }) { fabric ->
            FabricGridCard(
                fabric = fabric,
                distanceLabel = null,
                onClick = { onOpenDetails(fabric.id) },
                favorite = favoriteIds.contains(fabric.id),
                onFavoriteToggle = {
                    shellViewModel.toggleFavorite(fabric.id, favoriteIds.contains(fabric.id))
                }
            )
        }
    }
}
