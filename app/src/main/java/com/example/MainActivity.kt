package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmmViewModel
import com.example.ui.viewmodel.UiEvent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GoriberSmmApp()
            }
        }
    }
}

enum class SmmDestination(val title: String, val icon: ImageVector, val tag: String) {
    HOME("হোম", Icons.Default.Home, "nav_home"),
    NEW_ORDER("নতুন অর্ডার", Icons.Default.AddShoppingCart, "nav_new_order"),
    SERVICES("সার্ভিস", Icons.Default.FormatListBulleted, "nav_services"),
    ADD_FUNDS("ডিপোজিট", Icons.Default.AccountBalanceWallet, "nav_add_funds"),
    ORDERS("অর্ডার", Icons.Default.ReceiptLong, "nav_orders")
}

enum class SubScreen {
    NONE, ADMIN, TOOLS
}

@Composable
fun GoriberSmmApp() {
    val context = LocalContext.current
    val viewModel: SmmViewModel = viewModel()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is UiEvent.OpenUrl -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "ব্রাউজার খোলা সম্ভব হয়নি: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = SmmDarkBackground,
        bottomBar = {
            if (currentSubScreen == SubScreen.NONE) {
                NavigationBar(
                    containerColor = SmmDarkSurface,
                    tonalElevation = 8.dp
                ) {
                    SmmDestination.values().forEachIndexed { index, destination ->
                        NavigationBarItem(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = TextSecondary,
                                selectedTextColor = SmmPrimaryLight,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = SmmPrimary
                            ),
                            modifier = Modifier.testTag(destination.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = SmmDarkBackground
        ) {
            when (currentSubScreen) {
                SubScreen.ADMIN -> {
                    AdminScreen(
                        viewModel = viewModel,
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.TOOLS -> {
                    SmmToolsScreen(
                        onBack = { currentSubScreen = SubScreen.NONE }
                    )
                }
                SubScreen.NONE -> {
                    when (selectedTabIndex) {
                        0 -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToTab = { selectedTabIndex = it },
                            onOpenAdmin = { currentSubScreen = SubScreen.ADMIN },
                            onOpenTools = { currentSubScreen = SubScreen.TOOLS }
                        )
                        1 -> NewOrderScreen(
                            viewModel = viewModel,
                            onNavigateToAddFunds = { selectedTabIndex = 3 },
                            onOrderSuccess = { selectedTabIndex = 4 }
                        )
                        2 -> ServicesScreen(
                            viewModel = viewModel,
                            onSelectServiceAndOrder = { service ->
                                viewModel.selectService(service)
                                selectedTabIndex = 1 // Switch to New Order tab
                            }
                        )
                        3 -> AddFundsScreen(
                            viewModel = viewModel
                        )
                        4 -> OrdersScreen(
                            viewModel = viewModel,
                            onNavigateToNewOrder = { selectedTabIndex = 1 }
                        )
                    }
                }
            }
        }
    }
}
