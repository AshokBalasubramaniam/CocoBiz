package com.cocobiz.app.presentation.reports

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.domain.model.SaleStatus
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.ui.components.EmptyStateView
import com.cocobiz.app.ui.theme.AvatarColors
import com.cocobiz.app.util.DateUtils.toDisplayString
import com.cocobiz.app.util.toCurrencyString
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

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

    // ── Entrance animation states ─────────────────────────────────────
    // Filter tabs: fade + rise from 10dp below
    var filterVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { filterVisible = true }
    val filterProgress by animateFloatAsState(
        targetValue = if (filterVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "filterProgress"
    )

    // Revenue card: soft spring scale 0.92→1.0 + fade, delayed 180ms
    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(180)
        cardVisible = true
    }
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "cardAlpha"
    )
    val cardScale by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "cardScale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reports",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                actions = {
                    if (uiState.exportState == ExportState.EXPORTING) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .clickable { viewModel.exportPdf(context) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Export PDF",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // ── Segmented filter tabs: fade + rise ────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .graphicsLayer {
                            alpha = filterProgress
                            translationY = 10.dp.toPx() * (1f - filterProgress)
                        }
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isDark) Color(0xFF1C1C2E) else Color(0xFFF0F2F5))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ReportType.entries.forEach { type ->
                        val selected = uiState.reportType == type
                        val selectedBg = if (isDark) Color(0xFF3949AB) else MaterialTheme.colorScheme.primary
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(50.dp))
                                .background(if (selected) selectedBg else Color.Transparent)
                                .clickable { viewModel.updateReportType(type) }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = type.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // ── Month chips: staggered fade left→right ────────────────
            if (uiState.reportType == ReportType.MONTHLY) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // items(Int) is a direct LazyListScope function — no extra import needed
                        items(12) { monthIdx ->
                            AnimatedMonthChip(
                                month = monthIdx + 1,
                                selected = uiState.selectedMonth == monthIdx + 1,
                                onClick = { viewModel.updateMonth(monthIdx + 1) },
                                // stagger: first chip at 80ms, +40ms each
                                animDelay = 80 + (monthIdx * 40),
                                isDark = isDark
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── Revenue card: scale spring + fade ────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .graphicsLayer {
                            scaleX = cardScale
                            scaleY = cardScale
                            alpha = cardAlpha
                        },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDark)
                                    Brush.linearGradient(listOf(Color(0xFF111E4A), Color(0xFF1A2D6B)))
                                else
                                    Brush.linearGradient(listOf(Color(0xFFEEF2FF), Color(0xFFE8EDFF)))
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDark) Color(0xFF1E2F78)
                                        else Color(0xFF3949AB).copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF8BA4FF) else Color(0xFF1A237E),
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Total Revenue",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDark) Color(0xFF8BA4FF) else Color(0xFF3949AB).copy(alpha = 0.7f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    uiState.totalRevenue.toCurrencyString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Color.White else Color(0xFF1A237E)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${uiState.sales.size} transaction(s)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Color(0xFF6B8FFF) else Color(0xFF3949AB).copy(alpha = 0.6f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF1E2F78) else Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF8BA4FF) else Color(0xFF1A237E),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ── Sale rows: staggered slide-up + fade ──────────────────
            if (uiState.sales.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Assessment,
                        title = "No Data",
                        subtitle = "No sales found for the selected period",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            } else {
                itemsIndexed(
                    items = uiState.sales,
                    key = { _, sale -> sale.id }
                ) { index, sale ->
                    ReportSaleRow(
                        sale = sale,
                        isDark = isDark,
                        // Start after card finishes (180ms delay + ~300ms spring) → 480ms
                        // then stagger each row 80ms apart
                        animDelay = 480 + (index * 80)
                    )
                }
            }
        }
    }
}

// ── Month chip with staggered fade-in ────────────────────────────────────────

@Composable
private fun AnimatedMonthChip(
    month: Int,
    selected: Boolean,
    onClick: () -> Unit,
    animDelay: Int,
    isDark: Boolean
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(animDelay.toLong())
        visible = true
    }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "chipAlpha"
    )
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = Modifier.graphicsLayer { this.alpha = alpha },
        label = {
            Text(Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault()))
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = if (isDark) Color(0xFF3949AB) else MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White
        )
    )
}

// ── Sale row with slide-up + fade entrance ────────────────────────────────────

@Composable
private fun ReportSaleRow(
    sale: SalesEntry,
    isDark: Boolean,
    animDelay: Int = 0
) {
    val avatarColor = AvatarColors[(sale.id % AvatarColors.size).toInt()]
    val initials = sale.dealerName.trim().split(" ")
        .take(2).joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { "?" }

    // Animate once when this composable first enters composition.
    // LazyColumn keys items by sale.id so a given row only animates the
    // first time it appears — never on scroll-back.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(animDelay.toLong())
        visible = true
    }
    // Single 0→1 progress drives both alpha and translationY — one state read,
    // one graphicsLayer call, zero layout invalidations.
    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "rowProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .graphicsLayer {
                alpha = progress
                // Slides up from 20dp below its natural position
                translationY = 20.dp.toPx() * (1f - progress)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(avatarColor.copy(alpha = if (isDark) 0.35f else 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = avatarColor,
                    fontSize = 16.sp
                )
            }

            // Dealer info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    sale.dealerName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "${sale.dealerPlace} • ${sale.coconutType.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        sale.salesDate.toDisplayString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Amount + status badge
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    sale.totalAmount.toCurrencyString(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                val isActive = sale.status == SaleStatus.ACTIVE
                val badgeBg = when {
                    isDark && isActive  -> Color(0xFF1A3326)
                    isDark && !isActive -> Color(0xFF1E2D6B)
                    else                -> Color(0xFFEEEEEE)
                }
                val badgeText = when {
                    isDark && isActive  -> Color(0xFF4CAF50)
                    isDark && !isActive -> Color(0xFF8BA4FF)
                    else                -> Color(0xFF757575)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (isActive) "Active" else "Done",
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
