package com.cocobiz.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cocobiz.app.presentation.auth.ForgotPasswordScreen
import com.cocobiz.app.ui.components.FloatingBottomNavBar
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
import com.cocobiz.app.ui.theme.DashboardColors
import kotlinx.coroutines.launch

@Composable
fun CocoBizNavGraph(isLoggedIn: Boolean = false) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // The 4 bottom-nav tabs live as pages of one shared HorizontalPager (hosted at
    // Screen.Dashboard.route) so the user can swipe left/right between them, not
    // just tap the bottom bar. bottomNavItems' order defines the page order.
    val pagerState = rememberPagerState(pageCount = { bottomNavItems.size })
    val tabScope = rememberCoroutineScope()

    val showBottomBar = currentDestination?.route == Screen.Dashboard.route

    val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    Scaffold(
        containerColor = if (showBottomBar) DashboardColors.ShellMid else MaterialTheme.colorScheme.background,
        // Only reserve top (status bar) inset here. Tab screens' own FloatingBottomNavBar
        // already handles the bottom/navigation-bar inset itself; routes with no bottom
        // bar (auth screens, forms) are left free to paint their own content — including
        // edge-to-edge decorative art — all the way to the true bottom edge.
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            if (showBottomBar) {
                FloatingBottomNavBar(
                    items = bottomNavItems,
                    isSelected = { screen ->
                        bottomNavItems.indexOfFirst { it.screen == screen } == pagerState.currentPage
                    },
                    onItemClick = { screen ->
                        val index = bottomNavItems.indexOfFirst { it.screen == screen }
                        if (index >= 0) {
                            tabScope.launch { pagerState.animateScrollToPage(index) }
                        }
                    }
                )
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
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
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

            // ── Main app tabs (swipeable) ──────────────────────────────
            composable(Screen.Dashboard.route) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> DashboardScreen(
                            onAddSale = { navController.navigate(Screen.AddSale.route) },
                            onEditSale = { saleId -> navController.navigate(Screen.EditSale.createRoute(saleId)) },
                            onNavigateToDealers = { tabScope.launch { pagerState.animateScrollToPage(1) } },
                            onNavigateToReports = { tabScope.launch { pagerState.animateScrollToPage(2) } },
                            onNavigateToSettings = { tabScope.launch { pagerState.animateScrollToPage(3) } }
                        )
                        1 -> DealersScreen(
                            onAddDealer = { navController.navigate(Screen.AddDealer.route) },
                            onDealerClick = { dealerId -> navController.navigate(Screen.DealerProfile.createRoute(dealerId)) },
                            onEditDealer = { dealerId -> navController.navigate(Screen.EditDealer.createRoute(dealerId)) }
                        )
                        2 -> ReportsScreen()
                        3 -> SettingsScreen(
                            onNavigateToProfile = { navController.navigate(Screen.BusinessProfile.route) },
                            onNavigateToBackup = { navController.navigate(Screen.BackupRestore.route) },
                            onLogout = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
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
