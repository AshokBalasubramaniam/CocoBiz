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
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.cocobiz.app.ui.theme.GradientBrownEnd
import com.cocobiz.app.ui.theme.GradientBrownStart
import com.cocobiz.app.ui.theme.GradientGoldEnd
import com.cocobiz.app.ui.theme.GradientGoldStart
import com.cocobiz.app.ui.theme.GradientGreenEnd
import com.cocobiz.app.ui.theme.GradientGreenStart
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
                        Icon(Icons.Default.Edit, contentDescription = "Edit Dealer")
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
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val dealer = profileState.dealer ?: return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dealer avatar + info header
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                .background(Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dealer.dealerName.take(1).uppercase(),
                                style = MaterialTheme.typography.displaySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = dealer.dealerName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dealer.place,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))

                        // Contact chips
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (dealer.phone.isNotBlank()) {
                                ContactChip(Icons.Default.Phone, dealer.phone)
                            }
                            if (dealer.email.isNotBlank()) {
                                ContactChip(Icons.Default.Email, dealer.email)
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
                        title = "Total Revenue",
                        value = profileState.totalRevenue.toCurrencyString(),
                        gradientColors = listOf(GradientGoldStart, GradientGoldEnd),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileStatCard(
                        title = "Active Sales",
                        value = "${profileState.activeSalesCount}",
                        gradientColors = listOf(GradientGreenStart, GradientGreenEnd),
                        modifier = Modifier.weight(1f)
                    )
                    ProfileStatCard(
                        title = "Completed",
                        value = "${profileState.completedSalesCount}",
                        gradientColors = listOf(GradientBrownStart, GradientBrownEnd),
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
private fun ContactChip(icon: ImageVector, text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(12.dp)
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                Spacer(Modifier.height(4.dp))
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
