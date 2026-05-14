package com.kutira.kone.ui.navigation

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RequestQuote
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kutira.kone.models.UserRole
import com.kutira.kone.ui.screens.auth.AuthScreen
import com.kutira.kone.ui.screens.customer.CartScreen
import com.kutira.kone.ui.screens.customer.FabricMapScreen
import com.kutira.kone.ui.screens.customer.MarketplaceScreen
import com.kutira.kone.ui.screens.customer.WishlistScreen
import com.kutira.kone.ui.screens.detail.FabricDetailScreen
import com.kutira.kone.ui.screens.onboarding.OnboardingScreen
import com.kutira.kone.ui.screens.profile.ProfileScreen
import com.kutira.kone.ui.screens.role.RoleSelectionScreen
import com.kutira.kone.ui.screens.splash.SplashScreen
import com.kutira.kone.ui.screens.trades.AcceptedTradesScreen
import com.kutira.kone.ui.screens.trades.TradeRequestsScreen
import com.kutira.kone.ui.screens.vendor.UploadFabricScreen
import com.kutira.kone.ui.screens.vendor.VendorDashboardScreen
import com.kutira.kone.ui.viewmodel.CustomerShellViewModel
import com.kutira.kone.ui.viewmodel.MarketplaceViewModel
import com.kutira.kone.ui.viewmodel.SplashDestination
import com.kutira.kone.ui.viewmodel.TradeRequestsViewModel
import com.kutira.kone.ui.screens.customer.CheckoutScreen
import com.kutira.kone.ui.screens.profile.ShippedProductsScreen
import com.kutira.kone.ui.screens.customer.OrdersScreen
import com.kutira.kone.ui.viewmodel.CheckoutViewModel
import com.kutira.kone.ui.screens.vendor.VendorOrdersScreen
@Composable
fun KutiraNavHost() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {

        composable(NavRoutes.SPLASH) {

            SplashScreen(
                onNavigate = { dest ->

                    when (dest) {

                        SplashDestination.Onboarding -> {
                            navController.navigate(NavRoutes.ONBOARDING) {
                                popUpTo(NavRoutes.SPLASH) {
                                    inclusive = true
                                }
                            }
                        }

                        SplashDestination.Role -> {
                            navController.navigate(NavRoutes.ROLE) {
                                popUpTo(NavRoutes.SPLASH) {
                                    inclusive = true
                                }
                            }
                        }

                        SplashDestination.Auth -> {
                            navController.navigate(NavRoutes.AUTH) {
                                popUpTo(NavRoutes.SPLASH) {
                                    inclusive = true
                                }
                            }
                        }

                        is SplashDestination.Home -> {

                            val target =
                                if (dest.role == UserRole.VENDOR)
                                    NavRoutes.VENDOR_HOME
                                else
                                    NavRoutes.CUSTOMER_HOME

                            navController.navigate(target) {
                                popUpTo(NavRoutes.SPLASH) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.ONBOARDING) {

            OnboardingScreen(
                onFinished = {

                    navController.navigate(NavRoutes.ROLE) {
                        popUpTo(NavRoutes.ONBOARDING) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(NavRoutes.ROLE) {

            RoleSelectionScreen(
                onRoleChosen = {

                    navController.navigate(
                        NavRoutes.AUTH
                    )
                }
            )
        }

        composable(NavRoutes.AUTH) {

            AuthScreen(
                navController = navController,
                onAuthenticated = {

                    navController.navigate(NavRoutes.SPLASH) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(NavRoutes.VENDOR_HOME) {
            VendorShell(navController)
        }

        composable(NavRoutes.VENDOR_UPLOAD) {

            UploadFabricScreen(
                onBack = {
                    navController.popBackStack()
                },

                onUploaded = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.CUSTOMER_HOME) {
            CustomerShell(navController)
        }

        composable(
            route = NavRoutes.FABRIC_DETAIL_PATTERN,
            arguments = listOf(
                navArgument("fabricId") {
                    type = NavType.StringType
                }
            )
        ) {

            FabricDetailScreen(

                navController = navController,

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = NavRoutes.CART_PATTERN,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val userId =
                backStackEntry.arguments?.getString("userId") ?: ""

            CartScreen(
                userId = userId,
                navController = navController
            )
        }

        composable(
            route = NavRoutes.CHECKOUT
        ) {

            val cartItems =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<List<com.kutira.kone.models.CartItem>>("cartItems")
                    ?: emptyList()

            val totalAmount =
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Double>("totalAmount")
                    ?: 0.0

            val checkoutViewModel:
                    CheckoutViewModel = hiltViewModel()

            CheckoutScreen(
                navController = navController,
                cartItems = cartItems,
                totalAmount = totalAmount,

                onPlaceOrder = {
                        address,
                        phone,
                        paymentId ->

                    checkoutViewModel.placeOrder(

                        address = address,

                        phone = phone,

                        paymentId = paymentId,

                        totalAmount = totalAmount,

                        items = cartItems
                    )
                }
            )
        }

        composable("accepted_trades") {
            AcceptedTradesScreen(
                navController = navController
            )
        }

        composable(
            NavRoutes.ORDERS
        ) {

            OrdersScreen()
        }

        composable(
            NavRoutes.VENDOR_ORDERS
        ) {

            VendorOrdersScreen()
        }

        composable(
            NavRoutes.SHIPPED_PRODUCTS
        ) {

            ShippedProductsScreen(
                navController = navController
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VendorShell(nav: NavHostController) {

    var tab by remember {
        mutableIntStateOf(0)
    }

    val tradeVm: TradeRequestsViewModel = hiltViewModel()

    val badge by tradeVm.pendingBadge.collectAsStateWithLifecycle()

    Scaffold(

        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },
                    icon = {
                        Icon(
                            Icons.Outlined.Home,
                            contentDescription = null
                        )
                    },
                    label = { Text("Studio") }
                )

                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },

                    icon = {

                        BadgedBox(
                            badge = {

                                if (badge > 0) {
                                    Badge {
                                        Text(badge.toString())
                                    }
                                }
                            }
                        ) {

                            Icon(
                                Icons.Outlined.RequestQuote,
                                contentDescription = null
                            )
                        }
                    },

                    label = {
                        Text("Trades")
                    }
                )

                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { tab = 2 },

                    icon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Profile")
                    }
                )
            }
        }
    ) { padding ->

        Box(
            Modifier.padding(padding)
        ) {

            when (tab) {

                0 -> VendorDashboardScreen(
                    onUploadClick = {
                        nav.navigate(NavRoutes.VENDOR_UPLOAD)
                    }
                )

                1 -> TradeRequestsScreen()

                2 -> ProfileScreen(

                    navController = nav,

                    onLogout = {

                        nav.navigate(NavRoutes.SPLASH) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    }
                )

            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
@Composable
private fun CustomerShell(nav: NavHostController) {

    val locationPermissions =
        rememberMultiplePermissionsState(

            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

    LaunchedEffect(Unit) {

        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }

    var tab by remember {
        mutableIntStateOf(0)
    }

    val browseVm: MarketplaceViewModel = hiltViewModel()

    val shellVm: CustomerShellViewModel = hiltViewModel()

    val favoriteIds by shellVm.favoriteIds.collectAsStateWithLifecycle()

    LaunchedEffect(locationPermissions.allPermissionsGranted) {

        if (locationPermissions.allPermissionsGranted) {
            browseVm.refreshLocation()
        }
    }

    Scaffold(

        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { tab = 0 },

                    icon = {
                        Icon(
                            Icons.Outlined.Home,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Browse")
                    }
                )

                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { tab = 1 },

                    icon = {
                        Icon(
                            Icons.Outlined.Map,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Map")
                    }
                )

                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { tab = 2 },

                    icon = {
                        Icon(
                            Icons.Outlined.FavoriteBorder,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Saved")
                    }
                )

                NavigationBarItem(
                    selected = tab == 3,
                    onClick = {
                        nav.navigate(
                            NavRoutes.cart("demoUser")
                        )
                    },

                    icon = {
                        Icon(
                            Icons.Outlined.ShoppingCart,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Cart")
                    }
                )

                NavigationBarItem(
                    selected = tab == 4,
                    onClick = { tab = 4 },

                    icon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null
                        )
                    },

                    label = {
                        Text("Profile")
                    }
                )
            }
        }
    ) { padding ->

        Box(
            Modifier.padding(padding)
        ) {

            when (tab) {

                0 -> MarketplaceScreen(
                    viewModel = browseVm,
                    onOpenDetails = { id ->
                        nav.navigate(NavRoutes.fabricDetail(id))
                    },
                    favoriteIds = favoriteIds,
                    onToggleFavorite = { id, isFav ->
                        shellVm.toggleFavorite(id, isFav)
                    }
                )

                1 -> FabricMapScreen(
                    viewModel = browseVm,
                    onOpenDetails = { id ->
                        nav.navigate(NavRoutes.fabricDetail(id))
                    }
                )

                2 -> WishlistScreen(
                    onOpenDetails = { id ->
                        nav.navigate(NavRoutes.fabricDetail(id))
                    },
                    shellViewModel = shellVm
                )

                4 -> ProfileScreen(

                    navController = nav,

                    onLogout = {

                        nav.navigate(NavRoutes.SPLASH) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                    },

                    onAcceptedTradesClick = {
                        nav.navigate("accepted_trades")
                    }
                )
            }
        }
    }
}