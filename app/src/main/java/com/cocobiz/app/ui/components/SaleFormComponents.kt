package com.cocobiz.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.cocobiz.app.ui.theme.FigmaColors
import kotlin.math.roundToInt

/**
 * Reusable building blocks for the "Add New Sale" screen, styled to match the
 * "Duplicate Venum UI Design" Figma Make export exactly (colors, spacing, radii).
 */

@Composable
fun SectionHeader(
    icon: @Composable () -> Unit,
    iconBackground: Color,
    title: String,
    titleColor: Color,
    subtitle: String,
    subtitleColor: Color,
    bannerColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bannerColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
            Text(subtitle, fontSize = 12.sp, color = subtitleColor)
        }
    }
}

@Composable
fun SectionCard(
    icon: @Composable () -> Unit,
    iconBackground: Color,
    title: String,
    titleColor: Color,
    subtitle: String,
    subtitleColor: Color,
    bannerColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    contentSpacing: androidx.compose.ui.unit.Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FigmaColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            SectionHeader(
                icon = icon,
                iconBackground = iconBackground,
                title = title,
                titleColor = titleColor,
                subtitle = subtitle,
                subtitleColor = subtitleColor,
                bannerColor = bannerColor
            )
            HorizontalDivider(color = borderColor, thickness = 1.dp)
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(contentSpacing),
                content = content
            )
        }
    }
}

@Composable
fun FormInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    errorMessage: String = "",
    readOnly: Boolean = false,
    horizontalPadding: androidx.compose.ui.unit.Dp = 16.dp
) {
    val hasError = errorMessage.isNotBlank()
    val borderColor = if (hasError) FigmaColors.Red600 else FigmaColors.Gray200
    val stacked = label != null || !singleLine

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .background(FigmaColors.White, RoundedCornerShape(12.dp))
                .padding(horizontal = horizontalPadding, vertical = 12.dp),
            verticalAlignment = if (stacked) Alignment.Top else Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            leadingIcon?.invoke()
            Column(modifier = Modifier.weight(1f)) {
                if (label != null) {
                    Text(label, fontSize = 12.sp, color = FigmaColors.Gray400)
                    Spacer(Modifier.height(2.dp))
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    singleLine = singleLine,
                    minLines = minLines,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = FigmaColors.Gray900),
                    cursorBrush = SolidColor(FigmaColors.Blue600),
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                    decorationBox = { inner ->
                        Box {
                            if (value.isEmpty()) {
                                Text(placeholder, fontSize = 14.sp, color = FigmaColors.Gray400)
                            }
                            inner()
                        }
                    }
                )
            }
            trailingIcon?.invoke()
        }
        if (hasError) {
            Text(
                errorMessage,
                color = FigmaColors.Red600,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun NotesInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 3
) {
    FormInput(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier,
        leadingIcon = leadingIcon,
        singleLine = false,
        minLines = minLines
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemSubLabel: ((T) -> String)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    errorMessage: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = remember(value, items) {
        items.filter { value.isBlank() || itemLabel(it).contains(value, ignoreCase = true) }
    }
    val hasError = errorMessage.isNotBlank()
    val borderColor = if (hasError) FigmaColors.Red600 else FigmaColors.Gray200

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .background(FigmaColors.White, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                leadingIcon?.invoke()
                Box(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = FigmaColors.Gray900),
                        cursorBrush = SolidColor(FigmaColors.Blue600),
                        decorationBox = { inner ->
                            Box {
                                if (value.isEmpty()) {
                                    Text(placeholder, fontSize = 14.sp, color = FigmaColors.Gray400)
                                }
                                inner()
                            }
                        }
                    )
                }
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }

            if (filtered.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filtered.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(itemLabel(item), fontWeight = FontWeight.Medium, fontSize = 14.sp, color = FigmaColors.Gray700)
                                    itemSubLabel?.invoke(item)?.let {
                                        Text(it, fontSize = 12.sp, color = FigmaColors.Gray400)
                                    }
                                }
                            },
                            onClick = {
                                onItemSelected(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        if (hasError) {
            Text(
                errorMessage,
                color = FigmaColors.Red600,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    dateText: String,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
    dateIcon: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FigmaColors.Gray600)
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FigmaColors.Gray200, RoundedCornerShape(12.dp))
                .background(FigmaColors.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                dateIcon()
                Text(dateText, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = FigmaColors.Gray900)
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = FigmaColors.White,
                border = BorderStroke(1.dp, FigmaColors.Blue200),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onChangeClick() }
            ) {
                Text(
                    "Change",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FigmaColors.Blue600,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ReminderSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = 0,
        modifier = modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = FigmaColors.Blue600,
            activeTrackColor = FigmaColors.Blue600,
            inactiveTrackColor = FigmaColors.Gray200
        )
    )
}

@Composable
fun ReminderCycleSelector(
    days: Int,
    onDaysChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 7f..365f
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reminder Cycle", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = FigmaColors.Gray600)
            Text("$days Days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FigmaColors.Blue600)
        }
        ReminderSlider(
            value = days.toFloat(),
            onValueChange = { onDaysChange(it.roundToInt()) },
            valueRange = valueRange
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${valueRange.start.roundToInt()}d", fontSize = 12.sp, color = FigmaColors.Gray400)
            Text("${valueRange.endInclusive.roundToInt()}d", fontSize = 12.sp, color = FigmaColors.Gray400)
        }
    }
}

@Composable
fun InfoCard(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    containerColor: Color = FigmaColors.Blue50,
    labelColor: Color = FigmaColors.Gray400,
    valueColor: Color = FigmaColors.Blue600
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                icon()
                Column {
                    Text(label, fontSize = 12.sp, color = labelColor)
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
                }
            }
            if (badgeText != null) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = FigmaColors.White,
                    border = BorderStroke(1.dp, FigmaColors.Gray100)
                ) {
                    Text(
                        badgeText,
                        fontSize = 12.sp,
                        color = FigmaColors.Gray400,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

data class ToggleOption(
    val label: String,
    val iconPainter: Painter,
    val iconSize: Dp = 32.dp
)

@Composable
fun CoconutTypeSelector(
    options: List<ToggleOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (selected) FigmaColors.Blue50 else FigmaColors.White, RoundedCornerShape(12.dp))
                    .border(2.dp, if (selected) FigmaColors.Blue500 else FigmaColors.Gray200, RoundedCornerShape(12.dp))
                    .clickable { onSelect(index) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (selected) FigmaColors.Blue500 else FigmaColors.Gray300, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(FigmaColors.Blue500)
                            )
                        }
                    }
                    Text(
                        option.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selected) FigmaColors.Blue600 else FigmaColors.Gray500
                    )
                }
                Image(
                    painter = option.iconPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(option.iconSize)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = FigmaColors.Blue600,
            contentColor = FigmaColors.White,
            disabledContainerColor = FigmaColors.Blue600.copy(alpha = 0.6f),
            disabledContentColor = FigmaColors.White
        )
    ) {
        if (icon != null) {
            icon()
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
