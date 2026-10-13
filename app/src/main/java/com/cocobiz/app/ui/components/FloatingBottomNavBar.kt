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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocobiz.app.R
import com.cocobiz.app.ui.navigation.BottomNavItem
import com.cocobiz.app.ui.navigation.Screen
import com.cocobiz.app.ui.theme.DashboardColors

/**
 * Floating pill-shaped bottom navigation bar: a leafy transparent-PNG background
 * (leaves only at the far left/right, fully transparent center) sits behind a
 * rounded white capsule. The image uses [Modifier.matchParentSize] so it always
 * exactly matches the pill's real measured height (incl. the nav-bar inset) -
 * no hand-guessed height, so no gap/mismatch on any device.
 */
@Composable
fun FloatingBottomNavBar(
    items: List<BottomNavItem>,
    isSelected: (Screen) -> Boolean,
    onItemClick: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.bg_nav_leaves),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(26.dp),
            color = DashboardColors.White,
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = isSelected(item.screen)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) DashboardColors.GreenLight else Color.Transparent)
                            .clickable { onItemClick(item.screen) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (selected) DashboardColors.BrandGreen else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (selected) DashboardColors.White else DashboardColors.Gray700,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            item.label,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) DashboardColors.BrandGreen else DashboardColors.Gray700,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
