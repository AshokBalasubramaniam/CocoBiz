package com.cocobiz.app.presentation.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.R
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.ui.components.ConfirmationDialog
import com.cocobiz.app.ui.components.CtaBanner
import com.cocobiz.app.ui.components.DashboardSectionTitle
import com.cocobiz.app.ui.components.DashboardStatCard
import com.cocobiz.app.ui.components.EmptyStateView
import com.cocobiz.app.ui.components.GradientStatCard
import com.cocobiz.app.ui.components.GreetingHeader
import com.cocobiz.app.ui.components.HeroBanner
import com.cocobiz.app.ui.components.QuickActionButton
import com.cocobiz.app.ui.components.SalesCard
import com.cocobiz.app.ui.components.SalesTab
import com.cocobiz.app.ui.components.SalesTabRow
import com.cocobiz.app.ui.components.SectionHeader
import com.cocobiz.app.ui.components.UpcomingReminderCard
import com.cocobiz.app.ui.theme.DashboardColors
import com.cocobiz.app.ui.theme.StatBlue
import com.cocobiz.app.ui.theme.StatBlueLight
import com.cocobiz.app.ui.theme.StatOrange
import com.cocobiz.app.ui.theme.StatOrangeLight
import com.cocobiz.app.ui.theme.StatPurple
import com.cocobiz.app.ui.theme.StatPurpleLight
import com.cocobiz.app.ui.theme.StatTeal
import com.cocobiz.app.ui.theme.StatTealLight
import com.cocobiz.app.util.toCompactCurrencyString
import com.cocobiz.app.util.toCurrencyString
import com.cocobiz.app.util.toFormattedString
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddSale: () -> Unit,
    onEditSale: (Long) -> Unit,
    onNavigateToDealers: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var saleToDelete by remember { mutableStateOf<Long?>(null) }
    var saleToComplete by remember { mutableStateOf<Long?>(null) }
    var isSearchActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) focusRequester.requestFocus()
    }

    var statsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { statsVisible = true }
    val statsAlpha by animateFloatAsState(
        targetValue = if (statsVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "statsAlpha"
    )
    val statsScale by animateFloatAsState(
        targetValue = if (statsVisible) 1f else 0.92f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "statsScale"
    )

    if (saleToDelete != null) {
        ConfirmationDialog(
            title = "Delete Sale",
            message = "Are you sure you want to delete this sale? This action cannot be undone.",
            onConfirm = {
                viewModel.deleteSale(saleToDelete!!)
                saleToDelete = null
            },
            onDismiss = { saleToDelete = null }
        )
    }

    if (saleToComplete != null) {
        ConfirmationDialog(
            title = "Mark as Completed",
            message = "Mark this sale as completed? It will be moved to completed sales.",
            onConfirm = {
                viewModel.completeSale(saleToComplete!!)
                saleToComplete = null
            },
            onDismiss = { saleToComplete = null },
            confirmText = "Complete",
            confirmButtonColor = MaterialTheme.colorScheme.primary
        )
    }

    if (isSearchActive || uiState.viewMode != ViewMode.NORMAL) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (isSearchActive || uiState.viewMode != ViewMode.NORMAL) {
                        IconButton(onClick = {
                            if (isSearchActive) {
                                isSearchActive = false
                                viewModel.updateSearchQuery("")
                            } else {
                                viewModel.setViewMode(ViewMode.NORMAL)
                            }
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                title = {
                    if (isSearchActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            if (uiState.searchQuery.isEmpty()) {
                                Text(
                                    text = "Search dealers, places…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            BasicTextField(
                                value = uiState.searchQuery,
                                onValueChange = { viewModel.updateSearchQuery(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {})
                            )
                        }
                    } else {
                        when (uiState.viewMode) {
                            ViewMode.NEXT_HARVESTING -> Column {
                                Text(
                                    "Next Harvesting",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Sorted by earliest date",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            ViewMode.YEAR_REPORT -> Column {
                                Text(
                                    "Sales — Last 12 Months",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "${uiState.yearSales.size} sales · ₹${uiState.yearSales.sumOf { it.totalAmount }.toLong()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            ViewMode.NORMAL -> Column {
                                Text(
                                    "CocoBiz",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Coconut Sales Manager",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (isSearchActive) {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else if (uiState.viewMode == ViewMode.NORMAL) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (uiState.viewMode == ViewMode.NORMAL) {
                FloatingActionButton(
                    onClick = onAddSale,
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Sale", tint = Color.White)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Stat cards ────────────────────────────────────────────
            if (!isSearchActive) {
                item {
                    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(IntrinsicSize.Max)
                            .graphicsLayer {
                                scaleX = statsScale
                                scaleY = statsScale
                                alpha = statsAlpha
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GradientStatCard(
                            title = "Active Sales",
                            value = "${uiState.stats.totalActiveSales}",
                            icon = Icons.Default.ShoppingCart,
                            iconBackground = if (isDark) Color(0xFF1E4070) else StatBlueLight,
                            iconTint     = if (isDark) Color(0xFF64B5F6) else StatBlue,
                            valueColor   = if (isDark) Color(0xFF64B5F6) else StatBlue,
                            cardBackground = if (isDark) Color(0xFF182840) else Color(0xFFF0F5FF),
                            subtitle = "Ongoing deals",
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        GradientStatCard(
                            title = "Completed Sales",
                            value = "${uiState.stats.totalCompletedSales}",
                            icon = Icons.Default.CheckCircle,
                            iconBackground = if (isDark) Color(0xFF4A2800) else StatOrangeLight,
                            iconTint     = if (isDark) Color(0xFFFFB74D) else StatOrange,
                            valueColor   = if (isDark) Color(0xFFFFB74D) else StatOrange,
                            cardBackground = if (isDark) Color(0xFF2A1A08) else Color(0xFFFFF5EE),
                            subtitle = "This month",
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        GradientStatCard(
                            title = "Total Amount",
                            value = uiState.stats.totalRevenue.toCompactCurrencyString(),
                            icon = Icons.Default.AttachMoney,
                            iconBackground = if (isDark) Color(0xFF371655) else StatPurpleLight,
                            iconTint     = if (isDark) Color(0xFFCE93D8) else StatPurple,
                            valueColor   = if (isDark) Color(0xFFCE93D8) else StatPurple,
                            cardBackground = if (uiState.viewMode == ViewMode.YEAR_REPORT)
                                (if (isDark) Color(0xFF4A2080) else Color(0xFFE8D8FF))
                            else
                                (if (isDark) Color(0xFF221338) else Color(0xFFF5F0FF)),
                            subtitle = "Tap for year report",
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = {
                                viewModel.setViewMode(
                                    if (uiState.viewMode == ViewMode.YEAR_REPORT) ViewMode.NORMAL
                                    else ViewMode.YEAR_REPORT
                                )
                            }
                        )
                        GradientStatCard(
                            title = "Next Harvesting",
                            value = nextPaymentLabel(uiState),
                            icon = Icons.Default.CalendarToday,
                            iconBackground = if (isDark) Color(0xFF124038) else StatTealLight,
                            iconTint     = if (isDark) Color(0xFF4DB6AC) else StatTeal,
                            valueColor   = if (isDark) Color(0xFF4DB6AC) else StatTeal,
                            cardBackground = if (uiState.viewMode == ViewMode.NEXT_HARVESTING)
                                (if (isDark) Color(0xFF1A5050) else Color(0xFFCCF0EE))
                            else
                                (if (isDark) Color(0xFF0E2826) else Color(0xFFEFF9F9)),
                            subtitle = "${uiState.stats.upcomingReminders} due",
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = {
                                viewModel.setViewMode(
                                    if (uiState.viewMode == ViewMode.NEXT_HARVESTING) ViewMode.NORMAL
                                    else ViewMode.NEXT_HARVESTING
                                )
                            }
                        )
                    }
                }
            }

            // ── YEAR_REPORT mode ──────────────────────────────────────
            if (uiState.viewMode == ViewMode.YEAR_REPORT) {
                item {
                    SectionHeader(
                        title = "All Sales — Last 12 Months",
                        count = uiState.yearSales.size
                    )
                }
                if (uiState.yearSales.isEmpty()) {
                    item {
                        EmptyStateView(
                            icon = Icons.Default.AttachMoney,
                            title = "No sales in the last 12 months",
                            subtitle = "Add sales to see your annual report",
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                        )
                    }
                } else {
                    itemsIndexed(items = uiState.yearSales, key = { _, sale -> "yr_${sale.id}" }) { index, sale ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { delay((100 + index * 60).toLong()); visible = true }
                        val progress by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                            label = "yearSale$index"
                        )
                        SalesCard(
                            sale = sale,
                            onEdit = { onEditSale(sale.id) },
                            onDelete = { saleToDelete = sale.id },
                            onComplete = { saleToComplete = sale.id },
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .graphicsLayer {
                                    alpha = progress
                                    translationY = 20.dp.toPx() * (1f - progress)
                                }
                        )
                    }
                }
                return@LazyColumn
            }

            // ── Active Sales ──────────────────────────────────────────
            item {
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { delay(80); visible = true }
                val progress by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
                    label = "activeSectionHeader"
                )
                Box(modifier = Modifier.graphicsLayer {
                    alpha = progress
                    translationY = 10.dp.toPx() * (1f - progress)
                }) {
                    SectionHeader(
                        title = if (uiState.viewMode == ViewMode.NEXT_HARVESTING)
                            "Next Harvesting — All Active" else "Active Sales",
                        count = uiState.activeSales.size
                    )
                }
            }

            if (uiState.activeSales.isEmpty()) {
                item {
                    if (isSearchActive) {
                        EmptyStateView(
                            icon = Icons.Default.Search,
                            title = "No results",
                            subtitle = "No active sales match \"${uiState.searchQuery}\"",
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                        ) {
                            EmptyStateView(
                                icon = Icons.Default.ShoppingCart,
                                title = "No Active Sales",
                                subtitle = "Tap the + button to add your first coconut sale",
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onAddSale,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Add Sale")
                            }
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                }
            } else {
                itemsIndexed(items = uiState.activeSales, key = { _, sale -> sale.id }) { index, sale ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { delay((200 + index * 80).toLong()); visible = true }
                    val progress by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
                        label = "activeSale$index"
                    )
                    SalesCard(
                        sale = sale,
                        onEdit = { onEditSale(sale.id) },
                        onDelete = { saleToDelete = sale.id },
                        onComplete = { saleToComplete = sale.id },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .graphicsLayer {
                                alpha = progress
                                translationY = 20.dp.toPx() * (1f - progress)
                            }
                    )
                }
            }

            // ── Completed Sales (hidden in NEXT_HARVESTING mode) ──────
            if (uiState.viewMode != ViewMode.NEXT_HARVESTING) {
                item {
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) { delay(320); visible = true }
                    val progress by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
                        label = "completedSectionHeader"
                    )
                    Box(modifier = Modifier.graphicsLayer {
                        alpha = progress
                        translationY = 10.dp.toPx() * (1f - progress)
                    }) {
                        Spacer(Modifier.height(4.dp))
                        SectionHeader(
                            title = "Completed Sales",
                            count = uiState.completedSales.size,
                            onViewAll = {}
                        )
                    }
                }

                if (uiState.completedSales.isEmpty()) {
                    item {
                        if (isSearchActive && uiState.searchQuery.isNotEmpty()) {
                            EmptyStateView(
                                icon = Icons.Default.Search,
                                title = "No results",
                                subtitle = "No completed sales match \"${uiState.searchQuery}\"",
                                modifier = Modifier.fillMaxWidth().padding(24.dp)
                            )
                        } else {
                            EmptyStateView(
                                icon = Icons.Default.CheckCircle,
                                title = "No Completed Sales",
                                subtitle = "Completed sales will appear here",
                                modifier = Modifier.fillMaxWidth().padding(24.dp)
                            )
                        }
                    }
                } else {
                    itemsIndexed(items = uiState.completedSales, key = { _, sale -> sale.id }) { index, sale ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { delay((400 + index * 80).toLong()); visible = true }
                        val progress by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
                            label = "completedSale$index"
                        )
                        SalesCard(
                            sale = sale,
                            onEdit = {},
                            onDelete = { saleToDelete = sale.id },
                            onComplete = {},
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .graphicsLayer {
                                    alpha = progress
                                    translationY = 20.dp.toPx() * (1f - progress)
                                }
                        )
                    }
                }
            }
        }
    }
    } else {
        NormalDashboardContent(
            uiState = uiState,
            viewModel = viewModel,
            onAddSale = onAddSale,
            onEditSale = onEditSale,
            onNavigateToDealers = onNavigateToDealers,
            onNavigateToReports = onNavigateToReports,
            onNavigateToSettings = onNavigateToSettings,
            onSearchClick = { isSearchActive = true },
            onDeleteRequest = { saleToDelete = it },
            onCompleteRequest = { saleToComplete = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NormalDashboardContent(
    uiState: DashboardUiState,
    viewModel: DashboardViewModel,
    onAddSale: () -> Unit,
    onEditSale: (Long) -> Unit,
    onNavigateToDealers: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSearchClick: () -> Unit,
    onDeleteRequest: (Long) -> Unit,
    onCompleteRequest: (Long) -> Unit
) {
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
    }
    val firstName = uiState.ownerName.trim().split(" ").firstOrNull().orEmpty().ifBlank { "there" }
    val avatarInitial = firstName.take(1).uppercase().ifBlank { "?" }

    val tabs = listOf(
        SalesTab(FilterType.ACTIVE, "Active", uiState.stats.totalActiveSales),
        SalesTab(FilterType.COMPLETED, "Completed", uiState.stats.totalCompletedSales),
        SalesTab(FilterType.ALL, "All Sales", uiState.allSales.size)
    )
    val listForTab: List<SalesEntry> = when (uiState.filterType) {
        FilterType.ACTIVE -> uiState.activeSales
        FilterType.COMPLETED -> uiState.completedSales
        FilterType.ALL -> uiState.allSales
    }

    val nextReminder = uiState.upcomingSales.minByOrNull { it.nextSalesDate }

    Scaffold(
        containerColor = Color.Transparent,
        // The outer NavGraph Scaffold already reserves the status-bar/nav-bar
        // insets for this screen's content; consuming them again here would
        // double the top gap above the greeting header.
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(DashboardColors.ShellTop, DashboardColors.ShellMid, DashboardColors.ShellBottom)
                    )
                )
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                GreetingHeader(
                    greeting = greeting,
                    name = firstName,
                    onSearchClick = onSearchClick,
                    onNotificationsClick = { viewModel.setViewMode(ViewMode.NEXT_HARVESTING) },
                    avatarInitial = avatarInitial,
                    hasUnreadNotifications = uiState.stats.upcomingReminders > 0
                )
            }

            item {
                HeroBanner(heroImage = painterResource(R.drawable.ic_dashboard_hero))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            title = "Active Sales",
                            value = "${uiState.stats.totalActiveSales}",
                            subtitle = "Ongoing deals",
                            icon = Icons.Default.ShoppingCart,
                            iconBackground = DashboardColors.GreenLight,
                            accentColor = DashboardColors.BrandGreen,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = { viewModel.updateFilter(FilterType.ACTIVE) }
                        )
                        DashboardStatCard(
                            title = "Completed Sales",
                            value = "${uiState.stats.totalCompletedSales}",
                            subtitle = "This month",
                            icon = Icons.Default.CheckCircle,
                            iconBackground = DashboardColors.OrangeLight,
                            accentColor = DashboardColors.Orange,
                            valueColor = DashboardColors.Orange,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = { viewModel.updateFilter(FilterType.COMPLETED) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            title = "Total Amount",
                            value = uiState.stats.totalRevenue.toCompactCurrencyString(),
                            subtitle = "Tap for year report",
                            icon = Icons.Default.CurrencyRupee,
                            iconBackground = DashboardColors.PurpleLight,
                            accentColor = DashboardColors.Purple,
                            valueFontSize = 20.sp,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = { viewModel.setViewMode(ViewMode.YEAR_REPORT) }
                        )
                        DashboardStatCard(
                            title = "Next Harvest",
                            value = nextPaymentLabel(uiState),
                            subtitle = "${uiState.stats.upcomingReminders} due",
                            icon = Icons.Default.CalendarToday,
                            iconBackground = DashboardColors.BlueLight,
                            accentColor = DashboardColors.Blue,
                            valueFontSize = 18.sp,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            onClick = { viewModel.setViewMode(ViewMode.NEXT_HARVESTING) }
                        )
                    }
                }
            }

            item {
                CtaBanner(
                    onAddSaleClick = onAddSale,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardSectionTitle(
                        icon = Icons.Default.Add,
                        iconTint = DashboardColors.BrandGreen,
                        title = "Quick Actions"
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionButton(
                            label = "Add Sale",
                            background = DashboardColors.GreenLight,
                            onClick = onAddSale,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DashboardColors.BrandGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        QuickActionButton(
                            label = "Manage Dealers",
                            background = DashboardColors.BlueLight,
                            onClick = onNavigateToDealers,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, tint = DashboardColors.Blue, modifier = Modifier.size(26.dp))
                        }
                        QuickActionButton(
                            label = "View Reports",
                            background = DashboardColors.PurpleLight,
                            onClick = onNavigateToReports,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = DashboardColors.Purple, modifier = Modifier.size(26.dp))
                        }
                        QuickActionButton(
                            label = "App Settings",
                            background = DashboardColors.OrangeLight,
                            onClick = onNavigateToSettings,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = DashboardColors.Orange, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardSectionTitle(
                        icon = Icons.Default.CalendarToday,
                        iconTint = DashboardColors.BrandGreen,
                        title = "Upcoming Reminders",
                        actionLabel = "View all",
                        onAction = { viewModel.setViewMode(ViewMode.NEXT_HARVESTING) }
                    )
                    if (nextReminder != null) {
                        val unitLabel = if (nextReminder.coconutType == com.cocobiz.app.domain.model.CoconutType.TONNAGE) "Tons" else "Pcs"
                        UpcomingReminderCard(
                            dayNumber = nextReminder.nextSalesDate.dayOfMonth.toString(),
                            month = DateTimeFormatter.ofPattern("MMM", Locale.getDefault()).format(nextReminder.nextSalesDate),
                            title = "Sale due for ${nextReminder.dealerName}",
                            subtitle = "${nextReminder.quantity.toFormattedString()} $unitLabel · ${nextReminder.coconutType.displayName}",
                            dueBadge = if (nextReminder.isOverdue) "Overdue"
                                else if (nextReminder.remainingDays == 0L) "Due today"
                                else "In ${nextReminder.remainingDays} days"
                        )
                    } else {
                        Text(
                            "No upcoming reminders",
                            fontSize = 13.sp,
                            color = DashboardColors.Gray500
                        )
                    }
                }
            }

            item {
                SalesTabRow(
                    tabs = tabs,
                    selected = uiState.filterType,
                    onSelect = { viewModel.updateFilter(it) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            if (listForTab.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.ShoppingCart,
                        title = "No sales here yet",
                        subtitle = "Tap the Add Sale action above to record your first coconut sale",
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            } else {
                itemsIndexed(items = listForTab, key = { _, sale -> "${uiState.filterType}_${sale.id}" }) { index, sale ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(sale.id, uiState.filterType) { delay((index * 60).toLong()); visible = true }
                    val progress by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                        label = "dashSale$index"
                    )
                    SalesCard(
                        sale = sale,
                        onEdit = { onEditSale(sale.id) },
                        onDelete = { onDeleteRequest(sale.id) },
                        onComplete = { onCompleteRequest(sale.id) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .graphicsLayer {
                                alpha = progress
                                translationY = 16.dp.toPx() * (1f - progress)
                            }
                    )
                }
            }
        }
    }
}

private fun nextPaymentLabel(uiState: DashboardUiState): String {
    return uiState.nextHarvestDate?.let {
        DateTimeFormatter.ofPattern("d MMM yy", Locale.getDefault()).format(it)
    } ?: "—"
}
