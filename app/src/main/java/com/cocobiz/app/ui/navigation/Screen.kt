package com.cocobiz.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Bottom Nav
    data object Dashboard : Screen("dashboard")
    data object Dealers : Screen("dealers")
    data object Reports : Screen("reports")
    data object Settings : Screen("settings")

    // Detail screens
    data object AddSale : Screen("add_sale")
    data object EditSale : Screen("edit_sale/{saleId}") {
        fun createRoute(saleId: Long) = "edit_sale/$saleId"
    }
    data object DealerProfile : Screen("dealer_profile/{dealerId}") {
        fun createRoute(dealerId: Long) = "dealer_profile/$dealerId"
    }
    data object AddDealer : Screen("add_dealer")
    data object EditDealer : Screen("edit_dealer/{dealerId}") {
        fun createRoute(dealerId: Long) = "edit_dealer/$dealerId"
    }
    data object BusinessProfile : Screen("business_profile")
    data object BackupRestore : Screen("backup_restore")
    data object PdfReport : Screen("pdf_report")
    data object ExcelReport : Screen("excel_report")
    data object SaleDetail : Screen("sale_detail/{saleId}") {
        fun createRoute(saleId: Long) = "sale_detail/$saleId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Dashboard,
        label = "Dashboard",
        icon = Icons.Default.Dashboard
    ),
    BottomNavItem(
        screen = Screen.Dealers,
        label = "Dealers",
        icon = Icons.Default.People
    ),
    BottomNavItem(
        screen = Screen.Reports,
        label = "Reports",
        icon = Icons.Default.Assessment
    ),
    BottomNavItem(
        screen = Screen.Settings,
        label = "Settings",
        icon = Icons.Default.Settings
    )
)
