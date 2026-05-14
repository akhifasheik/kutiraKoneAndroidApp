package com.kutira.kone.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SwapCalls
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kutira.kone.models.CartItem
import com.kutira.kone.models.Fabric
import com.kutira.kone.ui.navigation.NavRoutes
import com.kutira.kone.ui.viewmodel.CartViewModel
import com.kutira.kone.ui.viewmodel.FabricDetailViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabricDetailScreen(
    navController: NavController,
    onBack: () -> Unit,
    viewModel: FabricDetailViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {

    val fabric by viewModel.fabric.collectAsStateWithLifecycle()

    val distanceLabel by
    viewModel.distanceLabel.collectAsStateWithLifecycle()

    val tradeMessage by
    viewModel.tradeMessage.collectAsStateWithLifecycle()

    val aiResult by
    viewModel.aiResult.collectAsStateWithLifecycle()

    val aiLoading by
    viewModel.aiLoading.collectAsStateWithLifecycle()

    val isFavorite by
    viewModel.favorite.collectAsStateWithLifecycle()

    val snackbarHostState =
        remember {
            SnackbarHostState()
        }

    LaunchedEffect(tradeMessage) {

        tradeMessage?.let {

            snackbarHostState.showSnackbar(it)

            viewModel.clearTradeMessage()
        }
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text("Fabric Details")
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite()
                        }
                    ) {

                        Icon(

                            imageVector =
                                if (isFavorite)
                                    Icons.Filled.Favorite
                                else
                                    Icons.Default.FavoriteBorder,

                            contentDescription = "Favorite",

                            tint =
                                if (isFavorite)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },

        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            fabric?.let { f ->

                AsyncImage(

                    model = f.imageUrl,

                    contentDescription =
                        f.materialType,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(
                            RoundedCornerShape(12.dp)
                        ),

                    contentScale =
                        ContentScale.Crop
                )

                Column(

                    verticalArrangement =
                        Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        f.materialType,
                        style =
                            MaterialTheme.typography.headlineMedium
                    )

                    distanceLabel?.let {

                        Text(
                            it,
                            style =
                                MaterialTheme.typography.bodyMedium,

                            color =
                                MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        "Size: ${f.size}",
                        style =
                            MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        "Color: ${f.color}",
                        style =
                            MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        "Price: ₹${f.price}",
                        style =
                            MaterialTheme.typography.titleLarge,

                        color =
                            MaterialTheme.colorScheme.secondary
                    )
                }

                Text(
                    f.description,
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                RowButtons(
                    navController,
                    viewModel,
                    cartViewModel,
                    f
                )

                if (aiLoading) {

                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }

                aiResult?.let {

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        colors =
                            CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.primaryContainer
                            )
                    ) {

                        Column(
                            Modifier.padding(16.dp)
                        ) {

                            Text(
                                "AI Design Ideas",
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                Modifier.height(8.dp)
                            )

                            Text(
                                it,
                                style =
                                    MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            } ?: Box(
                Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RowButtons(
    navController: NavController,
    viewModel: FabricDetailViewModel,
    cartViewModel: CartViewModel,
    fabric: Fabric
) {

    var showTradeDialog by remember {
        mutableStateOf(false)
    }

    var offeredItem by remember {
        mutableStateOf("")
    }

    var offeredDescription by remember {
        mutableStateOf("")
    }

    var offeredSize by remember {
        mutableStateOf("")
    }

    var offeredColor by remember {
        mutableStateOf("")
    }

    Column(
        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {

        Button(
            onClick = {
                showTradeDialog = true
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.SwapCalls,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text("Request Trade")
            }
        }

        OutlinedButton(

            onClick = {

                val singleCartItem = listOf(

                    CartItem(

                        cartItemId =
                            UUID.randomUUID().toString(),

                        customerId = "demoUser",

                        fabricId = fabric.id,

                        vendorId = fabric.sellerId,

                        fabricTitle =
                            fabric.materialType,

                        fabricImage =
                            fabric.imageUrl,

                        quantity = 1,

                        price =
                            fabric.price,

                        totalPrice =
                            fabric.price
                    )
                )

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        "cartItems",
                        singleCartItem
                    )

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(
                        "totalAmount",
                        fabric.price
                    )

                navController.navigate(
                    NavRoutes.CHECKOUT
                )
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text("Buy Scrap")
            }
        }

        Button(

            onClick = {

                val cartItem = CartItem(

                    cartItemId =
                        UUID.randomUUID().toString(),

                    customerId = "demoUser",

                    fabricId = fabric.id,

                    vendorId = fabric.sellerId,

                    fabricTitle =
                        fabric.materialType,

                    fabricImage =
                        fabric.imageUrl,

                    quantity = 1,

                    price =
                        fabric.price,

                    totalPrice =
                        fabric.price
                )

                cartViewModel.addToCart(
                    cartItem
                )
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.ShoppingCart,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text("Add To Cart")
            }
        }

        OutlinedButton(

            onClick = {
                viewModel.generateIdeas()
            },

            modifier =
                Modifier.fillMaxWidth()
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Outlined.AutoAwesome,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text("Generate Design Ideas")
            }
        }
    }

    if (showTradeDialog) {

        AlertDialog(

            onDismissRequest = {
                showTradeDialog = false
            },

            confirmButton = {

                Button(

                    onClick = {

                        viewModel.requestTrade(

                            offeredItem =
                                offeredItem,

                            offeredDescription =
                                offeredDescription,

                            offeredSize =
                                offeredSize,

                            offeredColor =
                                offeredColor,

                            tradeMessage =
                                offeredDescription
                        )

                        showTradeDialog = false
                    }
                ) {

                    Text("Send Trade Request")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showTradeDialog = false
                    }
                ) {

                    Text("Cancel")
                }
            },

            title = {
                Text("Your Trade Offer")
            },

            text = {

                Column(

                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedTextField(

                        value = offeredItem,

                        onValueChange = {
                            offeredItem = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Item Name")
                        }
                    )

                    OutlinedTextField(

                        value = offeredSize,

                        onValueChange = {
                            offeredSize = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Size")
                        }
                    )

                    OutlinedTextField(

                        value = offeredColor,

                        onValueChange = {
                            offeredColor = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Color")
                        }
                    )

                    OutlinedTextField(

                        value = offeredDescription,

                        onValueChange = {
                            offeredDescription = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        label = {
                            Text("Description")
                        }
                    )
                }
            }
        )
    }
}