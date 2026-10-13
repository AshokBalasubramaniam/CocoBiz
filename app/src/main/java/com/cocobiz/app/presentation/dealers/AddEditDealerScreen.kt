package com.cocobiz.app.presentation.dealers

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.ui.components.CocoBizTopBar
import com.cocobiz.app.ui.components.FormInput
import com.cocobiz.app.ui.components.NotesInput
import com.cocobiz.app.ui.components.PrimaryButton
import com.cocobiz.app.ui.components.SectionCard
import com.cocobiz.app.ui.theme.FigmaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDealerScreen(
    dealerId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: DealersViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val isEditMode = dealerId != null && dealerId != 0L
    val context = LocalContext.current

    LaunchedEffect(dealerId) {
        if (isEditMode) {
            viewModel.loadDealerForEdit(dealerId!!)
        } else {
            viewModel.resetForm()
        }
    }

    LaunchedEffect(formState.savedEventCount) {
        if (formState.savedEventCount > 0) {
            val msg = if (isEditMode) "Dealer updated successfully!" else "Dealer added successfully!"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CocoBizTopBar(
                title = if (isEditMode) "Edit Dealer" else "Add Dealer",
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
            item {
                SectionCard(
                    icon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = FigmaColors.Blue600, modifier = Modifier.size(18.dp))
                    },
                    iconBackground = FigmaColors.Blue100,
                    title = "Basic Information",
                    titleColor = FigmaColors.Blue700,
                    subtitle = "Enter dealer name and place",
                    subtitleColor = FigmaColors.Blue400,
                    bannerColor = FigmaColors.Blue50,
                    borderColor = FigmaColors.Blue100
                ) {
                    FormInput(
                        value = formState.dealerName,
                        onValueChange = { viewModel.updateFormField("name", it) },
                        placeholder = "Dealer Name *",
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        },
                        errorMessage = formState.nameError
                    )
                    FormInput(
                        value = formState.place,
                        onValueChange = { viewModel.updateFormField("place", it) },
                        placeholder = "Place *",
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        },
                        errorMessage = formState.placeError
                    )
                }
            }

            item {
                SectionCard(
                    icon = {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Green700, modifier = Modifier.size(18.dp))
                    },
                    iconBackground = FigmaColors.Green200,
                    title = "Contact Information",
                    titleColor = FigmaColors.Green800,
                    subtitle = "Phone, alternate number and email",
                    subtitleColor = FigmaColors.Green600,
                    bannerColor = FigmaColors.Green100,
                    borderColor = FigmaColors.Green200
                ) {
                    FormInput(
                        value = formState.phone,
                        onValueChange = { viewModel.updateFormField("phone", it) },
                        placeholder = "Phone Number *",
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        errorMessage = formState.phoneError
                    )
                    FormInput(
                        value = formState.alternatePhone,
                        onValueChange = { viewModel.updateFormField("altPhone", it) },
                        placeholder = "Alternate Phone",
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    FormInput(
                        value = formState.email,
                        onValueChange = { viewModel.updateFormField("email", it) },
                        placeholder = "Email",
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }
            }

            item {
                SectionCard(
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = null, tint = FigmaColors.Orange600, modifier = Modifier.size(18.dp))
                    },
                    iconBackground = FigmaColors.Orange100,
                    title = "Address & Notes",
                    titleColor = FigmaColors.Orange700,
                    subtitle = "Optional extra details",
                    subtitleColor = FigmaColors.Orange400,
                    bannerColor = FigmaColors.Orange50,
                    borderColor = FigmaColors.Orange100
                ) {
                    FormInput(
                        value = formState.address,
                        onValueChange = { viewModel.updateFormField("address", it) },
                        placeholder = "Address",
                        leadingIcon = {
                            Icon(Icons.Default.Home, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FormInput(
                            value = formState.city,
                            onValueChange = { viewModel.updateFormField("city", it) },
                            placeholder = "City",
                            leadingIcon = {
                                Icon(Icons.Default.LocationCity, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        FormInput(
                            value = formState.state,
                            onValueChange = { viewModel.updateFormField("state", it) },
                            placeholder = "State",
                            leadingIcon = {
                                Icon(Icons.Default.Map, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    FormInput(
                        value = formState.pincode,
                        onValueChange = { viewModel.updateFormField("pincode", it) },
                        placeholder = "Pincode",
                        leadingIcon = {
                            Icon(Icons.Default.Pin, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    NotesInput(
                        value = formState.notes,
                        onValueChange = { viewModel.updateFormField("notes", it) },
                        placeholder = "Any notes about this dealer...",
                        leadingIcon = {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }

            item {
                SectionCard(
                    icon = {
                        Icon(Icons.Default.ToggleOn, contentDescription = null, tint = FigmaColors.Purple700, modifier = Modifier.size(18.dp))
                    },
                    iconBackground = FigmaColors.Purple200,
                    title = "Status",
                    titleColor = FigmaColors.Purple900,
                    subtitle = "Toggle whether this dealer is active",
                    subtitleColor = FigmaColors.Purple600,
                    bannerColor = FigmaColors.Purple100,
                    borderColor = FigmaColors.Purple200
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = FigmaColors.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    if (formState.isActive) "Active" else "Inactive",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FigmaColors.Gray900
                                )
                                Text(
                                    "Excluded from active counts",
                                    fontSize = 11.sp,
                                    color = FigmaColors.Gray400
                                )
                            }
                            Switch(
                                checked = formState.isActive,
                                onCheckedChange = viewModel::updateIsActive,
                                colors = SwitchDefaults.colors(checkedTrackColor = FigmaColors.Purple600),
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                }
            }

            item {
                PrimaryButton(
                    text = if (formState.isSaving) "Saving..." else if (isEditMode) "Update Dealer" else "Add Dealer",
                    onClick = viewModel::saveDealer,
                    enabled = !formState.isSaving,
                    icon = {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                )
            }
        }
    }
}
