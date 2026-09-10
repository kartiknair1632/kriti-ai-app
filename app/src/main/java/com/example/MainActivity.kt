package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AssistantAndVaultScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MoneyScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.LifeOsTheme
import com.example.ui.viewmodel.LifeOsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemInDark) }

            LifeOsTheme(darkTheme = isDarkMode) {
                LifeOsApp(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeOsApp(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: LifeOsViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val statusNotification by viewModel.statusNotification.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val pendingTasksCount = tasks.count { it.status == "PENDING" }

    LaunchedEffect(statusNotification) {
        statusNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusNotification()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kriti AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // Tab 0: Home
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandPrimary,
                        selectedTextColor = BrandPrimary,
                        indicatorColor = BrandPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                // Tab 1: Tasks
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (pendingTasksCount > 0) {
                                    Badge(containerColor = BrandPrimary) {
                                        Text("$pendingTasksCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Tasks")
                        }
                    },
                    label = { Text("Tasks", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandPrimary,
                        selectedTextColor = BrandPrimary,
                        indicatorColor = BrandPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_tasks")
                )

                // Tab 2: Scan (Central Accent Hub)
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (selectedTab == 2) BrandPrimary else BrandPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Scan",
                                tint = if (selectedTab == 2) Color.White else BrandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    label = { Text("Scan", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandPrimary,
                        selectedTextColor = BrandPrimary,
                        indicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.testTag("nav_scan")
                )

                // Tab 3: Money
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = {
                        Icon(Icons.Default.Payments, contentDescription = "Money")
                    },
                    label = { Text("Money", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandPrimary,
                        selectedTextColor = BrandPrimary,
                        indicatorColor = BrandPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_money")
                )

                // Tab 4: Assistant & Vault
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = {
                        Icon(Icons.Default.SmartToy, contentDescription = "Life Hub")
                    },
                    label = { Text("Life Hub", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandPrimary,
                        selectedTextColor = BrandPrimary,
                        indicatorColor = BrandPrimary.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.testTag("nav_hub")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(targetState = selectedTab, label = "ScreenTransition") { tabIndex ->
                when (tabIndex) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { selectedTab = it }
                    )
                    1 -> TasksScreen(viewModel = viewModel)
                    2 -> ScanScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { selectedTab = it }
                    )
                    3 -> MoneyScreen(viewModel = viewModel)
                    4 -> AssistantAndVaultScreen(viewModel = viewModel)
                }
            }
        }
    }
}
