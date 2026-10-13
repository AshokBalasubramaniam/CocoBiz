package com.cocobiz.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocobiz.app.ui.theme.DashboardColors

/**
 * Reusable building blocks for the redesigned Dashboard screen, matching the
 * "Analyze and Develop UI" Figma Make export (colors, spacing, radii).
 */

@Composable
fun GreetingHeader(
    greeting: String,
    name: String,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    avatarInitial: String,
    modifier: Modifier = Modifier,
    hasUnreadNotifications: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(greeting, fontSize = 14.sp, color = DashboardColors.Gray600)
            Text(
                "$name 👋",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DashboardColors.Gray900
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoundIconButton(icon = Icons.Default.Search, onClick = onSearchClick)
            Box {
                RoundIconButton(icon = Icons.Default.Notifications, onClick = onNotificationsClick)
                if (hasUnreadNotifications) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp, end = 6.dp)
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(DashboardColors.Red500)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(DashboardColors.BrandGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(avatarInitial, color = DashboardColors.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun RoundIconButton(icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = DashboardColors.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(36.dp)) {
            Icon(icon, contentDescription = null, tint = DashboardColors.Gray600, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun HeroBanner(
    heroImage: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(DashboardColors.ShellTop, DashboardColors.ShellMid))
            )
    ) {
        Column(modifier = Modifier.padding(16.dp).width(200.dp)) {
            Text(
                "Fresh Deals\nBig Growth",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                color = DashboardColors.BrandGreen,
                lineHeight = 15.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "CocoBiz",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DashboardColors.BrandGreenTitle,
                lineHeight = 32.sp
            )
            Text(
                "Coconut Sales Manager",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = DashboardColors.Gray700
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Manage Today, Harvest a Better Tomorrow.",
                fontSize = 12.sp,
                color = DashboardColors.Gray500,
                lineHeight = 15.sp
            )
        }
        Image(
            painter = heroImage,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(140.dp)
        )
    }
}

@Composable
private fun MiniBarChart(color: Color, modifier: Modifier = Modifier) {
    val heights = listOf(12, 20, 16, 28, 24, 32, 20)
    Row(
        modifier = modifier.height(32.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        heights.forEachIndexed { index, h ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(h.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color.copy(alpha = 0.25f + index * 0.1f))
            )
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBackground: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    valueColor: Color = DashboardColors.Gray900,
    valueFontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                }
                MiniBarChart(color = accentColor)
            }
            Spacer(Modifier.height(8.dp))
            Text(title, fontSize = 12.sp, color = DashboardColors.Gray500)
            Text(value, fontSize = valueFontSize, fontWeight = FontWeight.Bold, color = valueColor)
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(subtitle, fontSize = 11.sp, color = DashboardColors.Gray400)
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = DashboardColors.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CtaBanner(
    onAddSaleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(DashboardColors.BannerGradientStart, DashboardColors.BannerGradientEnd))
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        Icons.Default.Eco,
                        contentDescription = null,
                        tint = DashboardColors.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text("Healthy Coconuts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DashboardColors.White, lineHeight = 16.sp)
                        Text("Happier Business!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DashboardColors.White, lineHeight = 16.sp)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("Keep tracking. Keep growing.", fontSize = 11.sp, color = DashboardColors.White.copy(alpha = 0.85f))
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = DashboardColors.White.copy(alpha = 0.15f),
                modifier = Modifier.clip(RoundedCornerShape(50)).clickable(onClick = onAddSaleClick)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = DashboardColors.White, modifier = Modifier.size(14.dp))
                    Text("Add New Sale", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DashboardColors.White)
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = DashboardColors.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    background: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon()
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = DashboardColors.Gray700,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

@Composable
fun UpcomingReminderCard(
    dayNumber: String,
    month: String,
    title: String,
    subtitle: String,
    dueBadge: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DashboardColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(44.dp)
            ) {
                Text(dayNumber, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = DashboardColors.Gray900)
                Text(month, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DashboardColors.Gray500)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(DashboardColors.Gray200)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DashboardColors.Gray900)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = DashboardColors.Gray500)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(shape = RoundedCornerShape(50), color = DashboardColors.OrangeLight) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = DashboardColors.Orange,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(dueBadge, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = DashboardColors.Orange)
                    }
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DashboardColors.Gray400,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardSectionTitle(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DashboardColors.Gray900)
        }
        if (actionLabel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = if (onAction != null) Modifier.clickable(onClick = onAction) else Modifier
            ) {
                Text(actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DashboardColors.Blue)
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DashboardColors.Blue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class SalesTab<T>(val value: T, val label: String, val count: Int)

@Composable
fun <T> SalesTabRow(
    tabs: List<SalesTab<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = tab.value == selected
                Column(
                    modifier = Modifier
                        .clickable { onSelect(tab.value) }
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            tab.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) DashboardColors.BrandGreen else DashboardColors.Gray500
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) DashboardColors.GreenLight else DashboardColors.Gray200
                        ) {
                            Text(
                                "${tab.count}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) DashboardColors.BrandGreen else DashboardColors.Gray500,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(if (isSelected) 56.dp else 0.dp)
                            .background(DashboardColors.BrandGreen)
                    )
                }
            }
        }
        HorizontalDivider(color = DashboardColors.Gray200, thickness = 1.dp)
    }
}
