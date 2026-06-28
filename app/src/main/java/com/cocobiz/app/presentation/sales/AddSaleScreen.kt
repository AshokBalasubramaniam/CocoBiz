package com.cocobiz.app.presentation.sales

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.domain.model.CoconutType
import com.cocobiz.app.ui.components.CocoBizTextField
import com.cocobiz.app.ui.components.CocoBizTopBar
import com.cocobiz.app.util.DateUtils.toDisplayString
import com.cocobiz.app.util.DateUtils.toLocalDate
import com.cocobiz.app.util.toCurrencyString
import android.widget.Toast
import java.time.Instant
import java.time.LocalDate
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

    var expandedDealerDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CocoBizTopBar(
                title = "Add New Sale",
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dealer Selection Section
            item {
                SaleFormSection(title = "Dealer Information") {
                    // Dealer dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedDealerDropdown,
                        onExpandedChange = { expandedDealerDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = formState.dealerName,
                            onValueChange = { viewModel.updateDealerName(it) },
                            label = { Text("Dealer Name *") },
                            placeholder = { Text("Select or type dealer name") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDealerDropdown) },
                            isError = formState.dealerNameError.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        if (formState.dealerNameError.isNotBlank()) {
                            Text(
                                formState.dealerNameError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        if (formState.dealers.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = expandedDealerDropdown,
                                onDismissRequest = { expandedDealerDropdown = false }
                            ) {
                                formState.dealers.filter {
                                    formState.dealerName.isBlank() ||
                                            it.dealerName.contains(formState.dealerName, ignoreCase = true)
                                }.forEach { dealer ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(dealer.dealerName, fontWeight = FontWeight.Medium)
                                                Text(
                                                    dealer.place,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectDealer(dealer)
                                            expandedDealerDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    CocoBizTextField(
                        value = formState.dealerPlace,
                        onValueChange = viewModel::updateDealerPlace,
                        label = "Dealer Place",
                        leadingIcon = Icons.Default.Place
                    )
                }
            }

            // Sales Date Section
            item {
                SaleFormSection(title = "Sale Information") {
                    // Date picker trigger
                    OutlinedTextField(
                        value = formState.salesDate.toDisplayString(),
                        onValueChange = {},
                        label = { Text("Sale Date *") },
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            TextButton(onClick = { viewModel.toggleDatePicker(true) }) {
                                Text("Change")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    // Cycle days slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Reminder Cycle",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "${formState.cycleDays} Days",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = formState.cycleDays.toFloat(),
                            onValueChange = { viewModel.updateCycleDays(it.toInt()) },
                            valueRange = 7f..365f,
                            steps = 0
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("7d", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("365d", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Next sales date (read only)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Next Sale Date",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    formState.nextSalesDate.toDisplayString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                "Auto-calculated",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Coconut Information
            item {
                SaleFormSection(title = "Coconut Information") {
                    // Coconut type selector
                    Text(
                        "Coconut Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CoconutType.entries.forEach { type ->
                            val isSelected = formState.coconutType == type
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateCoconutType(type) }
                                    .then(
                                        if (isSelected) Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(12.dp)
                                        ) else Modifier
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.updateCoconutType(type) }
                                    )
                                    Text(
                                        type.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CocoBizTextField(
                            value = formState.quantity,
                            onValueChange = viewModel::updateQuantity,
                            label = if (formState.coconutType == CoconutType.TONNAGE) "Quantity (Tons) *" else "Quantity (Pieces) *",
                            keyboardType = KeyboardType.Decimal,
                            isError = formState.quantityError.isNotBlank(),
                            errorMessage = formState.quantityError,
                            modifier = Modifier.weight(1f)
                        )
                        CocoBizTextField(
                            value = formState.rate,
                            onValueChange = viewModel::updateRate,
                            label = if (formState.coconutType == CoconutType.TONNAGE) "Rate/Ton (₹) *" else "Rate/Piece (₹) *",
                            keyboardType = KeyboardType.Decimal,
                            isError = formState.rateError.isNotBlank(),
                            errorMessage = formState.rateError,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Live total calculation
                    if (formState.totalAmount > 0) {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Total Amount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        "${formState.quantity.toDoubleOrNull()?.toLong() ?: 0} × ₹${formState.rate.toDoubleOrNull()?.toLong() ?: 0}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                                Text(
                                    formState.totalAmount.toCurrencyString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 22.sp
                                )
                            }
                        }
                    }
                }
            }

            // Notes
            item {
                SaleFormSection(title = "Additional Notes") {
                    CocoBizTextField(
                        value = formState.notes,
                        onValueChange = viewModel::updateNotes,
                        label = "Notes (Optional)",
                        maxLines = 4,
                        placeholder = "Any additional information..."
                    )
                }
            }

            // Save Button
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = viewModel::saveSale,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !formState.isSaving
                ) {
                    Text(
                        if (formState.isSaving) "Saving..." else "Save Sale",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Date Picker Dialog
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
private fun SaleFormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.primaryContainer)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
