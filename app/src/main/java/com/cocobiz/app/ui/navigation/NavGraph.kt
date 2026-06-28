package com.cocobiz.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cocobiz.app.presentation.auth.ForgotPasswordScreen
import com.cocobiz.app.presentation.auth.LoginScreen
import com.cocobiz.app.presentation.auth.RegisterScreen
import com.cocobiz.app.presentation.dashboard.DashboardScreen
import com.cocobiz.app.presentation.dealers.AddEditDealerScreen
import com.cocobiz.app.presentation.dealers.DealerProfileScreen
import com.cocobiz.app.presentation.dealers.DealersScreen
import com.cocobiz.app.presentation.profile.BusinessProfileScreen
import com.cocobiz.app.presentation.reports.ReportsScreen
import com.cocobiz.app.presentation.sales.AddSaleScreen
import com.cocobiz.app.presentation.sales.EditSaleScreen
import com.cocobiz.app.presentation.settings.BackupRestoreScreen
import com.cocobiz.app.presentation.settings.SettingsScreen

@Composable
fun CocoBizNavGraph(isLoggedIn: Boolean = false) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Dealers.route,
        Screen.Reports.route,
        Screen.Settings.route
    )
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = androidx.compose.ui.unit.Dp(3f)
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                )
            }
        ) {
            // ── Auth screens (no bottom nav) ──────────────────────────
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(
                    onResetSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // ── Main app screens ──────────────────────────────────────
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onAddSale = { navController.navigate(Screen.AddSale.route) },
                    onEditSale = { saleId -> navController.navigate(Screen.EditSale.createRoute(saleId)) }
                )
            }
            composable(Screen.Dealers.route) {
                DealersScreen(
                    onAddDealer = { navController.navigate(Screen.AddDealer.route) },
                    onDealerClick = { dealerId -> navController.navigate(Screen.DealerProfile.createRoute(dealerId)) },
                    onEditDealer = { dealerId -> navController.navigate(Screen.EditDealer.createRoute(dealerId)) }
                )
            }
            composable(Screen.Reports.route) {
                ReportsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToProfile = { navController.navigate(Screen.BusinessProfile.route) },
                    onNavigateToBackup = { navController.navigate(Screen.BackupRestore.route) },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.AddSale.route) {
                AddSaleScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.EditSale.route,
                arguments = listOf(navArgument("saleId") { type = NavType.LongType })
            ) { backStackEntry ->
                val saleId = backStackEntry.arguments?.getLong("saleId") ?: 0L
                EditSaleScreen(
                    saleId = saleId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.DealerProfile.route,
                arguments = listOf(navArgument("dealerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val dealerId = backStackEntry.arguments?.getLong("dealerId") ?: 0L
                DealerProfileScreen(
                    dealerId = dealerId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditDealer = { navController.navigate(Screen.EditDealer.createRoute(dealerId)) }
                )
            }
            composable(Screen.AddDealer.route) {
                AddEditDealerScreen(
                    dealerId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.EditDealer.route,
                arguments = listOf(navArgument("dealerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val dealerId = backStackEntry.arguments?.getLong("dealerId") ?: 0L
                AddEditDealerScreen(
                    dealerId = dealerId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.BusinessProfile.route) {
                BusinessProfileScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.BackupRestore.route) {
                BackupRestoreScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onResetComplete = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
