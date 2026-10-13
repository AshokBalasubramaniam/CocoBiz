package com.cocobiz.app.presentation.dealers

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
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
import com.cocobiz.app.domain.model.Dealer
import com.cocobiz.app.ui.components.ConfirmationDialog
import com.cocobiz.app.ui.components.EmptyStateView
import com.cocobiz.app.ui.theme.DealersColors
import kotlinx.coroutines.delay

@Composable
private fun avatarPalette() = listOf(
    DealersColors.Emerald500,
    DealersColors.Blue400,
    DealersColors.Orange400,
    DealersColors.Purple400
)

@Composable
fun DealersScreen(
    onAddDealer: () -> Unit,
    onDealerClick: (Long) -> Unit,
    onEditDealer: (Long) -> Unit,
    viewModel: DealersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var dealerToDelete by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    if (dealerToDelete != null) {
        ConfirmationDialog(
            title = "Delete Dealer",
            message = "Delete this dealer? All associated sales will also be deleted.",
            onConfirm = {
                viewModel.deleteDealer(dealerToDelete!!)
                dealerToDelete = null
            },
            onDismiss = { dealerToDelete = null }
        )
    }

    Scaffold(
        containerColor = DealersColors.White,
        // The outer NavGraph Scaffold already reserves the status-bar/nav-bar
        // insets for this screen's content; consuming them again here would
        // double the top gap above the header.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            GradientFab(onClick = onAddDealer)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                DealersHeader(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::updateSearchQuery,
                    statusFilter = uiState.statusFilter,
                    onStatusFilterChange = viewModel::updateStatusFilter
                )
            }

            item {
                DealerStatsRow(
                    total = uiState.totalCount,
                    active = uiState.activeCount,
                    inactive = uiState.inactiveCount
                )
            }

            item {
                DealersListHeader(
                    count = uiState.dealers.size,
                    sortType = uiState.sortType,
                    onSortChange = viewModel::updateSort
                )
            }

            if (uiState.dealers.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.People,
                        title = "No Dealers Yet",
                        subtitle = "Add your first dealer to get started",
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            } else {
                itemsIndexed(
                    items = uiState.dealers,
                    key = { _, dealer -> dealer.id }
                ) { index, dealer ->
                    var visible by remember { mutableStateOf(false) }
                    LaunchedEffect(dealer.id) { delay((60 + index * 60).toLong()); visible = true }
                    val progress by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                        label = "dealer$index"
                    )
                    DealerCard(
                        dealer = dealer,
                        avatarColor = avatarPalette().let { it[(dealer.id % it.size).toInt()] },
                        onClick = { onDealerClick(dealer.id) },
                        onDelete = { dealerToDelete = dealer.id },
                        onCall = {
                            if (dealer.phone.isNotBlank()) {
                                context.startActivity(
                                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${dealer.phone}"))
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
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

@Composable
private fun DealersHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    statusFilter: DealerStatusFilter,
    onStatusFilterChange: (DealerStatusFilter) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        DealersColors.HeaderGradientTop,
                        DealersColors.HeaderGradientMid,
                        DealersColors.HeaderGradientEnd
                    )
                )
            )
            .padding(bottom = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 20.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Manage Your", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = DealersColors.Gray500)
                    Text(
                        "Dealers",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = DealersColors.Gray900,
                        lineHeight = 32.sp
                    )
                    Text(
                        "Your trusted partners\nfor a better harvest.",
                        fontSize = 14.sp,
                        color = DealersColors.Gray500,
                        lineHeight = 18.sp
                    )
                }
                Box(modifier = Modifier.width(150.dp)) {
                    Text(
                        "Grow\nTogether",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = DealersColors.Emerald700,
                        lineHeight = 15.sp,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_dashboard_hero),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(width = 150.dp, height = 110.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DealersColors.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = DealersColors.Gray400, modifier = Modifier.size(20.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = DealersColors.Gray600),
                                cursorBrush = SolidColor(DealersColors.Emerald600),
                                decorationBox = { inner ->
                                    Box {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                "Search dealers by name, place or phone...",
                                                fontSize = 14.sp,
                                                color = DealersColors.Gray400,
                                                maxLines = 1
                                            )
                                        }
                                        inner()
                                    }
                                }
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = DealersColors.Gray400,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onSearchQueryChange("") }
                            )
                        }
                    }
                }

                var filterExpanded by remember { mutableStateOf(false) }
                Box {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DealersColors.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { filterExpanded = true }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = DealersColors.Emerald700, modifier = Modifier.size(20.dp))
                        }
                    }
                    DropdownMenu(expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                        DealerStatusFilter.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label, fontWeight = if (option == statusFilter) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    onStatusFilterChange(option)
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DealerStatsRow(total: Int, active: Int, inactive: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DealerStatCard(
            label = "Total Dealers",
            value = total,
            icon = Icons.Default.People,
            containerColor = DealersColors.Green50,
            iconBackground = DealersColors.Green100,
            iconTint = DealersColors.Emerald600,
            valueColor = DealersColors.Gray900,
            modifier = Modifier.weight(1f)
        )
        DealerStatCard(
            label = "Active Dealers",
            value = active,
            icon = Icons.Default.Place,
            containerColor = DealersColors.Blue50,
            iconBackground = DealersColors.Blue100,
            iconTint = DealersColors.Blue600,
            valueColor = DealersColors.Gray900,
            modifier = Modifier.weight(1f)
        )
        DealerStatCard(
            label = "Inactive",
            value = inactive,
            icon = Icons.Default.Person,
            containerColor = DealersColors.Orange50,
            iconBackground = DealersColors.Orange100,
            iconTint = DealersColors.Orange500,
            valueColor = DealersColors.Orange500,
            modifier = Modifier.weight(1f),

        )
    }
}

@Composable
private fun DealerStatCard(
    label: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    iconBackground: Color,
    iconTint: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                fontSize = 11.sp,
                color = DealersColors.Gray500,
                lineHeight = 13.sp,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Text("$value", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
private fun DealersListHeader(
    count: Int,
    sortType: DealerSortType,
    onSortChange: (DealerSortType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DealersColors.Emerald600)
            )
            Text("All Dealers", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DealersColors.Gray900)
            Surface(shape = RoundedCornerShape(50), color = DealersColors.Emerald100) {
                Text(
                    "$count",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DealersColors.Emerald700,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        var sortExpanded by remember { mutableStateOf(false) }
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable { sortExpanded = true }
            ) {
                Text("Sort by", fontSize = 12.sp, color = DealersColors.Gray500)
                Text(sortType.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DealersColors.Gray700)
            }
            DropdownMenu(expanded = sortExpanded, onDismissRequest = { sortExpanded = false }) {
                DealerSortType.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label, fontWeight = if (option == sortType) FontWeight.Bold else FontWeight.Normal) },
                        onClick = {
                            onSortChange(option)
                            sortExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DealerCard(
    dealer: Dealer,
    avatarColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = DealersColors.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            // Main Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 80.dp), // space for status
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = dealer.dealerName
                            .take(1)
                            .uppercase(),
                        fontSize = 15.sp,
                        color = DealersColors.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    // Dealer Name
                    Text(
                        text = dealer.dealerName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DealersColors.Gray900,
                        maxLines = 1
                    )

                    Spacer(Modifier.height(3.dp))

                    // Place
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = DealersColors.Gray400,
                            modifier = Modifier.size(11.dp)
                        )

                        Text(
                            text = dealer.place,
                            fontSize = 11.sp,
                            color = DealersColors.Gray500,
                            maxLines = 1
                        )
                    }

                    // Phone
                    if (dealer.phone.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                Icons.Default.Phone,
                                contentDescription = null,
                                tint = DealersColors.Gray400,
                                modifier = Modifier.size(11.dp)
                            )

                            Text(
                                text = dealer.phone,
                                fontSize = 11.sp,
                                color = DealersColors.Gray500,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // STATUS + ARROW → RIGHT END
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (dealer.isActive)
                        DealersColors.Emerald50
                    else
                        DealersColors.Orange50
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (dealer.isActive)
                                        DealersColors.Emerald500
                                    else
                                        DealersColors.Orange400
                                )
                        )

                        Text(
                            text = if (dealer.isActive) "Active" else "Inactive",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (dealer.isActive)
                                DealersColors.Emerald700
                            else
                                DealersColors.Orange500
                        )
                    }
                }

                // Arrow
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DealersColors.Gray400,
                    modifier = Modifier.size(18.dp)
                )
            }

            // ACTION BUTTONS → RIGHT BOTTOM
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DealerActionButton(
                    icon = Icons.Default.Call,
                    background = DealersColors.Emerald50,
                    tint = DealersColors.Emerald600,
                    onClick = onCall
                )

                DealerActionButton(
                    icon = Icons.Default.Delete,
                    background = DealersColors.Red50,
                    tint = DealersColors.Red400,
                    onClick = onDelete
                )
            }
        }
    }
}

@Composable
private fun DealerActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    background: Color,
    tint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
    }
}

@Composable
private fun GradientFab(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(DealersColors.FabGradientStart, DealersColors.FabGradientEnd)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Dealer", tint = DealersColors.White, modifier = Modifier.size(26.dp))
    }
}
