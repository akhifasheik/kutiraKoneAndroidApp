package com.kutira.kone.ui.screens.profile

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.kutira.kone.models.Fabric
import com.kutira.kone.models.UserRole
import com.kutira.kone.ui.navigation.NavRoutes
import com.kutira.kone.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onAcceptedTradesClick: (() -> Unit)? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val phone by viewModel.phone.collectAsState()

    val role by viewModel.role.collectAsState()

    val listings by
    viewModel.myListings.collectAsState()

    var pendingDelete by remember {
        mutableStateOf<Fabric?>(null)
    }

    var deleteError by remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        "Profile",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    ) { padding ->

        LazyColumn(

            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),

            contentPadding =
                PaddingValues(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            item {

                Text(
                    "Signed in as",
                    style =
                        MaterialTheme.typography.labelMedium
                )

                Text(

                    phone ?: "Unknown phone",

                    style =
                        MaterialTheme.typography.titleLarge,

                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    Modifier.height(4.dp)
                )

                Text(

                    "Role: ${role?.name ?: "Unknown"}",

                    style =
                        MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    Modifier.height(12.dp)
                )

                OutlinedButton(

                    onClick = {
                        viewModel.logout(onLogout)
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text("Log out")
                }

                Spacer(
                    Modifier.height(8.dp)
                )

                Button(

                    onClick = {

                        navController.navigate(
                            NavRoutes.SHIPPED_PRODUCTS
                        )
                    },

                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text("View Shipped Products")
                }

                Spacer(
                    Modifier.height(8.dp)
                )
                if (role != UserRole.VENDOR) {

                    Button(

                        onClick = {

                            navController.navigate(
                                NavRoutes.ORDERS
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text("My Orders")
                    }

                    Spacer(
                        Modifier.height(8.dp)
                    )
                }
                onAcceptedTradesClick?.let {

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Button(

                        onClick = it,

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text("Accepted Trades")
                    }
                }

                if (role == UserRole.VENDOR) {

                    Spacer(
                        Modifier.height(8.dp)
                    )

                    Button(

                        onClick = {

                            navController.navigate(
                                NavRoutes.VENDOR_ORDERS
                            )
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text("View Customer Orders")
                    }
                }
            }

            if (role == UserRole.VENDOR) {

                item {

                    Text(

                        "Your live listings",

                        style =
                            MaterialTheme.typography.titleMedium,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }

                items(

                    listings,

                    key = { it.id }

                ) { fabric ->

                    Card(

                        elevation =
                            CardDefaults.cardElevation(3.dp),

                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Column(
                            Modifier.padding(12.dp)
                        ) {

                            AsyncImage(

                                model =
                                    fabric.imageUrl,

                                contentDescription = null,

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            )

                            Spacer(
                                Modifier.height(8.dp)
                            )

                            Text(

                                fabric.materialType,

                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Spacer(
                                Modifier.height(8.dp)
                            )

                            Text(
                                "₹${fabric.price}"
                            )

                            Spacer(
                                Modifier.height(12.dp)
                            )

                            Button(

                                onClick = {
                                    pendingDelete = fabric
                                },

                                modifier =
                                    Modifier.fillMaxWidth()
                            ) {

                                Text("Delete listing")
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { fabric ->

        AlertDialog(

            onDismissRequest = {
                pendingDelete = null
            },

            title = {
                Text("Delete listing?")
            },

            text = {

                Text(
                    "This removes the fabric from Firestore and Storage."
                )
            },

            confirmButton = {

                Button(

                    onClick = {

                        viewModel.deleteListing(
                            fabric
                        ) { err ->

                            deleteError = err

                            pendingDelete = null
                        }
                    }
                ) {

                    Text("Delete")
                }
            },

            dismissButton = {

                OutlinedButton(

                    onClick = {
                        pendingDelete = null
                    }
                ) {

                    Text("Cancel")
                }
            }
        )
    }

    deleteError?.let { err ->

        AlertDialog(

            onDismissRequest = {
                deleteError = null
            },

            title = {
                Text("Could not delete")
            },

            text = {
                Text(err)
            },

            confirmButton = {

                Button(

                    onClick = {
                        deleteError = null
                    }
                ) {

                    Text("OK")
                }
            }
        )
    }
}