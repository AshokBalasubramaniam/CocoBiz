package com.cocobiz.app.presentation.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.R
import com.cocobiz.app.domain.model.SaleStatus
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.ui.components.EmptyStateView
import com.cocobiz.app.ui.theme.ReportsColors
import com.cocobiz.app.util.DateUtils.toDisplayString
import com.cocobiz.app.util.toCurrencyString
import com.cocobiz.app.util.toFormattedString
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
private fun avatarPalette() = listOf(
    ReportsColors.AvatarPurple,
    ReportsColors.AvatarGreen,
    ReportsColors.AvatarOrange,
    ReportsColors.AvatarBlue,
    ReportsColors.AvatarRed
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.exportState) {
        when (uiState.exportState) {
            ExportState.SUCCESS -> {
                snackbarHostState.showSnackbar("PDF saved to Downloads/CocoBiz")
                viewModel.resetExportState()
            }
            ExportState.FAILED -> {
                snackbarHostState.showSnackbar("Failed to export PDF. Please try again.")
                viewModel.resetExportState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        // The outer NavGraph Scaffold already reserves the status-bar/nav-bar insets.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(ReportsColors.PageGradientTop, ReportsColors.PageGradientMid, ReportsColors.PageGradientEnd)
                    )
                )
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ReportsHeader(
                    isExporting = uiState.exportState == ExportState.EXPORTING,
                    onExportClick = { viewModel.exportPdf(context) }
                )
            }

            item {
                PeriodTabsRow(
                    selected = uiState.reportType,
                    onSelect = viewModel::updateReportType
                )
            }

            if (uiState.reportType == ReportType.MONTHLY) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(12) { monthIdx ->
                            MonthChip(
                                month = monthIdx + 1,
                                selected = uiState.selectedMonth == monthIdx + 1,
                                onClick = { viewModel.updateMonth(monthIdx + 1) }
                            )
                        }
                    }
                }
            }

            item {
                TotalRevenueCard(
                    totalRevenue = uiState.totalRevenue,
                    transactionCount = uiState.sales.size,
                    trendPercent = uiState.trendPercent,
                    sparklineValues = uiState.sparklineValues,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                ReportStatsRow(
                    totalTransactions = uiState.sales.size,
                    totalQuantity = uiState.totalQuantity,
                    averageOrder = uiState.averageOrder
                )
            }

            item {
                Text(
                    "Transactions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ReportsColors.Slate900,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                )
            }

            if (uiState.sales.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Assessment,
                        title = "No Data",
                        subtitle = "No sales found for the selected period",
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            } else {
                items(items = uiState.sales, key = { it.id }) { sale ->
                    TransactionRow(
                        sale = sale,
                        avatarColor = avatarPalette().let { it[(sale.id % it.size).toInt()] },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportsHeader(
    isExporting: Boolean,
    onExportClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Reports", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = ReportsColors.Slate900, lineHeight = 30.sp)
            Text(
                "Track your business growth\nwith detailed insights",
                fontSize = 13.sp,
                color = ReportsColors.Slate500,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Box(modifier = Modifier.width(130.dp).height(100.dp)) {
            Text(
                "Grow\nTogether",
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = ReportsColors.Green600,
                lineHeight = 15.sp,
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Image(
                painter = painterResource(R.drawable.ic_dashboard_hero),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(width = 120.dp, height = 90.dp)
            )
            Surface(
                shape = CircleShape,
                color = ReportsColors.White,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .then(if (!isExporting) Modifier.clickable(onClick = onExportClick) else Modifier)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = ReportsColors.Green600)
                    } else {
                        Icon(Icons.Default.Download, contentDescription = "Export PDF", tint = ReportsColors.Gray700, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodTabsRow(
    selected: ReportType,
    onSelect: (ReportType) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = ReportsColors.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ReportType.entries.forEach { type ->
                val isSelected = selected == type
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ReportsColors.Green600 else Color.Transparent)
                        .clickable { onSelect(type) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = if (isSelected) ReportsColors.White else ReportsColors.Gray500,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        type.displayName,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ReportsColors.White else ReportsColors.Gray500,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthChip(
    month: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = ReportsColors.White,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selected) ReportsColors.Green600 else ReportsColors.Gray200),
        modifier = Modifier.clip(RoundedCornerShape(50)).clickable(onClick = onClick)
    ) {
        Text(
            Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) ReportsColors.Green600 else ReportsColors.Gray500,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun TotalRevenueCard(
    totalRevenue: Double,
    transactionCount: Int,
    trendPercent: Double?,
    sparklineValues: List<Double>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, ReportsColors.GreenBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(ReportsColors.GreenCardStart, ReportsColors.GreenCardEnd)))
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(ReportsColors.GreenBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = ReportsColors.Green600, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Total Revenue", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ReportsColors.Green600)
                Text(totalRevenue.toCurrencyString(), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = ReportsColors.Slate900)
                Text("$transactionCount transaction(s)", fontSize = 12.sp, color = ReportsColors.Slate500)
            }
            Column(horizontalAlignment = Alignment.End) {
                if (trendPercent != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ReportsColors.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ReportsColors.GreenBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                if (trendPercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = ReportsColors.Green600,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "${if (trendPercent >= 0) "+" else ""}${trendPercent.toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ReportsColors.Green600
                            )
                        }
                    }
                    Text("vs last month", fontSize = 10.sp, color = ReportsColors.Slate400, modifier = Modifier.padding(top = 2.dp))
                }
                Spacer(Modifier.height(4.dp))
                Sparkline(values = sparklineValues, modifier = Modifier.size(width = 80.dp, height = 32.dp))
            }
        }
    }
}

@Composable
private fun Sparkline(values: List<Double>, modifier: Modifier = Modifier) {
    if (values.size < 2) return
    val maxV = values.max().coerceAtLeast(1.0)
    val minV = values.min()
    val range = (maxV - minV).takeIf { it > 0 } ?: 1.0
    val lineColor = ReportsColors.Green600
    Canvas(modifier = modifier) {
        val stepX = size.width / (values.size - 1)
        val points = values.mapIndexed { index, v ->
            val x = index * stepX
            val y = size.height - ((v - minV) / range * size.height).toFloat()
            Offset(x, y)
        }
        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor.copy(alpha = 0.6f),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 2.5f
            )
        }
    }
}

@Composable
private fun ReportStatsRow(
    totalTransactions: Int,
    totalQuantity: Double,
    averageOrder: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReportStatCard(icon = Icons.Default.Receipt, label = "Transactions", value = "$totalTransactions", modifier = Modifier.weight(1f))
        ReportStatCard(icon = Icons.Default.Inventory2, label = "Quantity", value = totalQuantity.toFormattedString(), modifier = Modifier.weight(1f))
        ReportStatCard(icon = Icons.Default.CurrencyRupee, label = "Average Order", value = averageOrder.toCurrencyString(), modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ReportStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ReportsColors.White,
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = ReportsColors.Green600, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 10.sp, color = ReportsColors.Slate500, lineHeight = 12.sp, maxLines = 2)
            Text(
                value,
                fontSize = if (value.length > 6) 14.sp else 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ReportsColors.Slate900,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TransactionRow(
    sale: SalesEntry,
    avatarColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ReportsColors.White,
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    sale.dealerName.take(1).uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ReportsColors.Gray700
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(sale.dealerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReportsColors.Slate900, maxLines = 1)
                Text(
                    "${sale.quantity.toFormattedString()} • ${sale.coconutType.displayName}",
                    fontSize = 12.sp,
                    color = ReportsColors.Slate500
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = ReportsColors.Slate400, modifier = Modifier.size(10.dp))
                    Text(sale.salesDate.toDisplayString(), fontSize = 11.sp, color = ReportsColors.Slate400)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(sale.totalAmount.toCurrencyString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ReportsColors.Slate900)
                val isActive = sale.status == SaleStatus.ACTIVE
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isActive) ReportsColors.StatusActiveBg else ReportsColors.StatusDoneBg
                ) {
                    Text(
                        if (isActive) "Active" else "Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) ReportsColors.StatusActiveText else ReportsColors.StatusDoneText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
