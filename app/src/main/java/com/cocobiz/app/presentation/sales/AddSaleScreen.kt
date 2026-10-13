package com.cocobiz.app.presentation.sales

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.R
import com.cocobiz.app.domain.model.CoconutType
import com.cocobiz.app.ui.components.CoconutTypeSelector
import com.cocobiz.app.ui.components.DatePickerField
import com.cocobiz.app.ui.components.DropdownInput
import com.cocobiz.app.ui.components.FormInput
import com.cocobiz.app.ui.components.InfoCard
import com.cocobiz.app.ui.components.NotesInput
import com.cocobiz.app.ui.components.PrimaryButton
import com.cocobiz.app.ui.components.ReminderCycleSelector
import com.cocobiz.app.ui.components.SectionCard
import com.cocobiz.app.ui.components.ToggleOption
import com.cocobiz.app.ui.theme.FigmaColors
import com.cocobiz.app.util.DateUtils.toDisplayString
import com.cocobiz.app.util.toCurrencyString
import android.widget.Toast
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddSaleViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(formState.isSaved) {
        if (formState.isSaved) {
            val msg = if (formState.isEditMode) "Sale updated successfully!" else "Sale saved successfully!"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = FigmaColors.Gray100,
        topBar = {
            AddSaleHeader(
                onNavigateBack = onNavigateBack,
                title = if (formState.isEditMode) "Edit Sale" else "Add New Sale"
            )
        },
        bottomBar = {
            AddSaleSaveBar(
                isSaving = formState.isSaving,
                onSave = viewModel::saveSale
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionCard(
                    icon = {
                        Text(
                            text = "👤",
                            fontSize = 25.sp
                        )
                    },
                    iconBackground = FigmaColors.Blue100,
                    title = "Dealer Information",
                    titleColor = FigmaColors.Blue700,
                    subtitle = "Enter dealer details",
                    subtitleColor = FigmaColors.Blue400,
                    bannerColor = FigmaColors.Blue50,
                    borderColor = FigmaColors.Blue100
                ) {
                    DropdownInput(
                        value = formState.dealerName,
                        onValueChange = viewModel::updateDealerName,
                        placeholder = "Dealer Name *",
                        items = formState.dealers,
                        itemLabel = { it.dealerName },
                        itemSubLabel = { it.place },
                        onItemSelected = viewModel::selectDealer,
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.ic_person_line),
                                contentDescription = null,
                                tint = FigmaColors.Gray400,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        errorMessage = formState.dealerNameError
                    )
                    FormInput(
                        value = formState.dealerPlace,
                        onValueChange = viewModel::updateDealerPlace,
                        placeholder = "Dealer Place",
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.ic_location_line),
                                contentDescription = null,
                                tint = FigmaColors.Gray400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            item {
                SectionCard(
                    icon = {
                        Text(
                            text = "📅",
                            fontSize = 25.sp
                        )
                    },
                    iconBackground = FigmaColors.Green200,
                    title = "Sale Information",
                    titleColor = FigmaColors.Green800,
                    subtitle = "Set sale date and reminder cycle",
                    subtitleColor = FigmaColors.Green600,
                    bannerColor = FigmaColors.Green100,
                    borderColor = FigmaColors.Green200,
                    contentSpacing = 16.dp
                ) {
                    DatePickerField(
                        label = "Sale Date *",
                        dateText = formState.salesDate.toDisplayString(),
                        onChangeClick = { viewModel.toggleDatePicker(true) },
                        dateIcon = {
                            Icon(
                                painterResource(R.drawable.ic_calendar_line),
                                contentDescription = null,
                                tint = FigmaColors.Blue500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    ReminderCycleSelector(
                        days = formState.cycleDays,
                        onDaysChange = viewModel::updateCycleDays
                    )
                    InfoCard(
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_clock_line),
                                contentDescription = null,
                                tint = FigmaColors.Blue400,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = "Next Sale Date",
                        value = formState.nextSalesDate.toDisplayString(),
                        badgeText = "Auto-calculated",
                        containerColor = FigmaColors.Blue50
                    )
                }
            }

            item {
                SectionCard(
                    icon = {
                        Text(
                            text = "🥥",
                            fontSize = 30.sp
                        )
                    },
                    iconBackground = FigmaColors.Orange100,
                    title = "Coconut Information",
                    titleColor = FigmaColors.Orange700,
                    subtitle = "Select type and enter quantity",
                    subtitleColor = FigmaColors.Orange400,
                    bannerColor = FigmaColors.Orange50,
                    borderColor = FigmaColors.Orange100
                ) {
                    Text(
                        "Coconut Type",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FigmaColors.Gray600
                    )
                    CoconutTypeSelector(
                        options = listOf(
                            ToggleOption("Tonnage", painterResource(R.drawable.ic_truck_icon)),
                            ToggleOption("Single", painterResource(R.drawable.ic_coconut_piece_icon), iconSize = 44.dp)
                        ),
                        selectedIndex = CoconutType.entries.indexOf(formState.coconutType),
                        onSelect = { index -> viewModel.updateCoconutType(CoconutType.entries[index]) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FormInput(
                            value = formState.quantity,
                            onValueChange = viewModel::updateQuantity,
                            placeholder = "quantity",
                            label = if (formState.coconutType == CoconutType.TONNAGE) "Quantity (Tons)" else "Quantity (Pcs)",
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.ic_weight_line),
                                    contentDescription = null,
                                    tint = FigmaColors.Gray400,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            errorMessage = formState.quantityError,
                            horizontalPadding = 12.dp,
                            modifier = Modifier.weight(1f)
                        )
                        FormInput(
                            value = formState.rate,
                            onValueChange = viewModel::updateRate,
                            placeholder = "Enter rate",
                            label = if (formState.coconutType == CoconutType.TONNAGE) "Rate/Ton (₹) *" else "Rate/Pc (₹) *",
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.ic_rupee_line),
                                    contentDescription = null,
                                    tint = FigmaColors.Gray400,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            errorMessage = formState.rateError,
                            horizontalPadding = 12.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (formState.totalAmount > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FigmaColors.Orange50,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Total Amount", fontSize = 12.sp, color = FigmaColors.Orange600)
                                    Text(
                                        "${formState.quantity.toDoubleOrNull()?.toLong() ?: 0} × ₹${formState.rate.toDoubleOrNull()?.toLong() ?: 0}",
                                        fontSize = 12.sp,
                                        color = FigmaColors.Orange600.copy(alpha = 0.7f)
                                    )
                                }
                                Text(
                                    formState.totalAmount.toCurrencyString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FigmaColors.Orange700
                                )
                            }
                        }
                    }
                }
            }

            item {
                SectionCard(
                    icon = {
                        Text(
                            text = "📝",
                            fontSize = 25.sp
                        )
                    },
                    iconBackground = FigmaColors.Purple200,
                    title = "Additional Notes",
                    titleColor = FigmaColors.Purple900,
                    subtitle = "Add any extra details (optional)",
                    subtitleColor = FigmaColors.Purple600,
                    bannerColor = FigmaColors.Purple100,
                    borderColor = FigmaColors.Purple200
                ) {
                    NotesInput(
                        value = formState.notes,
                        onValueChange = viewModel::updateNotes,
                        placeholder = "Notes (Optional)",
                        leadingIcon = {
                            Icon(
                                painterResource(R.drawable.ic_note_line),
                                contentDescription = null,
                                tint = FigmaColors.Gray400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }
    }

    if (formState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.salesDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { viewModel.toggleDatePicker(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            viewModel.updateSalesDate(localDate)
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.toggleDatePicker(false) }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun AddSaleHeader(
    onNavigateBack: () -> Unit,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp)
            .background(FigmaColors.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(FigmaColors.Blue50)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(R.drawable.ic_arrow_left_line),
                    contentDescription = "Back",
                    tint = FigmaColors.Blue600,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FigmaColors.Gray900
                )
                Text(
                    "Record your coconut sale details",
                    fontSize = 12.sp,
                    color = FigmaColors.Gray400
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.ic_coconut_hero),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = 64.dp, height = 56.dp)
        )
    }
}

@Composable
private fun AddSaleSaveBar(
    isSaving: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp)
            .background(FigmaColors.White)
    ) {
        HorizontalDivider(color = FigmaColors.Gray100, thickness = 1.dp)
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            PrimaryButton(
                text = if (isSaving) "Saving..." else "Save Sale",
                onClick = onSave,
                enabled = !isSaving,
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_save_line),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
    }
}
