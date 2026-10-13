package com.cocobiz.app.presentation.dealers

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.ui.components.CocoBizTopBar
import com.cocobiz.app.ui.components.SalesCard
import com.cocobiz.app.ui.components.SectionHeader
import com.cocobiz.app.ui.theme.DealersColors
import com.cocobiz.app.util.toCurrencyString

@Composable
fun DealerProfileScreen(
    dealerId: Long,
    onNavigateBack: () -> Unit,
    onEditDealer: () -> Unit,
    viewModel: DealersViewModel = hiltViewModel()
) {
    val profileState by viewModel.dealerProfileState.collectAsStateWithLifecycle()

    LaunchedEffect(dealerId) {
        viewModel.loadDealerProfile(dealerId)
    }

    Scaffold(
        topBar = {
            CocoBizTopBar(
                title = profileState.dealer?.dealerName ?: "Dealer Profile",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onEditDealer) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Dealer", tint = DealersColors.Emerald600)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (profileState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DealersColors.Emerald600)
            }
            return@Scaffold
        }

        val dealer = profileState.dealer ?: return@Scaffold
        val avatarColor = listOf(
            DealersColors.Emerald700, DealersColors.Blue600, DealersColors.Orange500, DealersColors.Purple600
        )[(dealer.id % 4).toInt()]

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dealer avatar + info header
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DealersColors.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dealer.dealerName.take(1).uppercase(),
                                fontSize = 30.sp,
                                color = DealersColors.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(dealer.dealerName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DealersColors.Gray900)
                        Text(dealer.place, fontSize = 14.sp, color = DealersColors.Gray500)
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (dealer.isActive) DealersColors.Emerald50 else DealersColors.Orange50
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (dealer.isActive) DealersColors.Emerald500 else DealersColors.Orange400)
                                )
                                Text(
                                    if (dealer.isActive) "Active" else "Inactive",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (dealer.isActive) DealersColors.Emerald700 else DealersColors.Orange500
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Contact chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (dealer.phone.isNotBlank()) {
                                ContactChip(Icons.Default.Phone, dealer.phone, DealersColors.Emerald50, DealersColors.Emerald600)
                            }
                            if (dealer.email.isNotBlank()) {
                                ContactChip(Icons.Default.Email, dealer.email, DealersColors.Blue50, DealersColors.Blue600)
                            }
                        }
                    }
                }
            }

            // Revenue stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        icon = Icons.Default.AttachMoney,
                        title = "Total Revenue",
                        value = profileState.totalRevenue.toCurrencyString(),
                        containerColor = DealersColors.Green50,
                        iconBackground = DealersColors.Green100,
                        iconTint = DealersColors.Emerald600,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        icon = Icons.Default.ShoppingCart,
                        title = "Active Sales",
                        value = "${profileState.activeSalesCount}",
                        containerColor = DealersColors.Blue50,
                        iconBackground = DealersColors.Blue100,
                        iconTint = DealersColors.Blue600,
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        icon = Icons.Default.CheckCircle,
                        title = "Completed",
                        value = "${profileState.completedSalesCount}",
                        containerColor = DealersColors.Orange50,
                        iconBackground = DealersColors.Orange100,
                        iconTint = DealersColors.Orange500,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Sales history
            item {
                SectionHeader(
                    title = "Sales History",
                    count = profileState.dealerSales.size
                )
            }

            items(
                items = profileState.dealerSales,
                key = { it.id }
            ) { sale ->
                SalesCard(
                    sale = sale,
                    onEdit = {},
                    onDelete = {},
                    onComplete = {}
                )
            }
        }
    }
}

@Composable
private fun ContactChip(icon: ImageVector, text: String, background: Color, tint: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = tint)
        }
    }
}

@Composable
private fun ProfileStatCard(
    icon: ImageVector,
    title: String,
    value: String,
    containerColor: Color,
    iconBackground: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 11.sp, color = DealersColors.Gray500)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DealersColors.Gray900)
        }
    }
}
