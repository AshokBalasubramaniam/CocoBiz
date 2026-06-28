package com.cocobiz.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cocobiz.app.domain.model.CoconutType
import com.cocobiz.app.domain.model.SaleStatus
import com.cocobiz.app.domain.model.SalesEntry
import com.cocobiz.app.domain.model.StatusColor
import com.cocobiz.app.ui.theme.AvatarColors
import com.cocobiz.app.ui.theme.StatusGray
import com.cocobiz.app.ui.theme.StatusGreen
import com.cocobiz.app.ui.theme.StatusRed
import com.cocobiz.app.ui.theme.StatusYellow
import com.cocobiz.app.util.toCurrencyString
import java.time.format.DateTimeFormatter

private val cardDateFmt = DateTimeFormatter.ofPattern("d MMM yyyy")

@Composable
fun SalesCard(
    sale: SalesEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (sale.statusColor) {
        StatusColor.GREEN -> StatusGreen
        StatusColor.YELLOW -> StatusYellow
        StatusColor.RED -> StatusRed
        StatusColor.GRAY -> StatusGray
    }

    val animatedStatusColor by animateColorAsState(
        targetValue = statusColor,
        animationSpec = tween(300),
        label = "statusColor"
    )

    val avatarColor = AvatarColors[(sale.id % AvatarColors.size).toInt()]
    // Ring uses avatar color for completed sales (status color for active to show urgency)
    val ringColor = if (sale.status == SaleStatus.COMPLETED) avatarColor else animatedStatusColor
    val initials = sale.dealerName.trim().split(" ")
        .take(2).joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { "?" }

    val unitLabel = if (sale.coconutType == CoconutType.TONNAGE) "Tons" else "Pcs"
    val rateLabel = if (sale.coconutType == CoconutType.TONNAGE) "/Ton" else "/Pc"

    val progress = if (sale.cycleDays > 0) {
        (sale.remainingDays.toFloat() / sale.cycleDays.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Header ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Initials avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = sale.dealerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = sale.dealerPlace,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Status badge + circular countdown
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(StatusGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (sale.status == SaleStatus.ACTIVE) "Active" else "Done",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    CircularCountdownIndicator(
                        progress = progress,
                        remainingDays = sale.remainingDays,
                        totalDays = sale.cycleDays,
                        statusColor = ringColor,
                        size = 56.dp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(12.dp))

            // ── 4-column detail row ───────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SaleDetailCol(label = "Type", value = sale.coconutType.displayName, modifier = Modifier.weight(1f))
                SaleDetailCol(
                    label = "Qty",
                    value = "${sale.quantity.toFormattedString()} $unitLabel",
                    modifier = Modifier.weight(1f)
                )
                SaleDetailCol(
                    label = "Rate",
                    value = "₹${sale.rate.toLong()}$rateLabel",
                    modifier = Modifier.weight(1f)
                )
                SaleDetailCol(
                    label = "Total Amount",
                    value = sale.totalAmount.toCurrencyString(),
                    valueColor = avatarColor,
                    modifier = Modifier.weight(1.3f),
                    alignEnd = true
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Footer row ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Next: ${sale.nextSalesDate.format(cardDateFmt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (sale.status == SaleStatus.COMPLETED)
                        "Completed on: ${sale.salesDate.format(cardDateFmt)}"
                    else
                        "Sale on: ${sale.salesDate.format(cardDateFmt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Action buttons (active only) ──────────────────────────
            if (sale.status == SaleStatus.ACTIVE) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                ) {
                    ActionIconButton(
                        onClick = onDelete,
                        background = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    ActionIconButton(
                        onClick = onEdit,
                        background = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    ActionIconButton(
                        onClick = onComplete,
                        background = StatusGreen.copy(alpha = 0.15f)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Complete",
                            tint = StatusGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SaleDetailCol(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
    alignEnd: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ActionIconButton(
    onClick: () -> Unit,
    background: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private fun Double.toFormattedString(): String =
    if (this == kotlin.math.floor(this)) "%.0f".format(this) else "%.1f".format(this)
