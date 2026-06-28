package com.cocobiz.app.presentation.dealers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.ui.components.CocoBizTextField
import com.cocobiz.app.ui.components.CocoBizTopBar

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
                DealerFormSection(title = "Basic Information") {
                    CocoBizTextField(
                        value = formState.dealerName,
                        onValueChange = { viewModel.updateFormField("name", it) },
                        label = "Dealer Name *",
                        leadingIcon = Icons.Default.Person,
                        isError = formState.nameError.isNotBlank(),
                        errorMessage = formState.nameError
                    )
                    Spacer(Modifier.height(8.dp))
                    CocoBizTextField(
                        value = formState.place,
                        onValueChange = { viewModel.updateFormField("place", it) },
                        label = "Place *",
                        leadingIcon = Icons.Default.Place,
                        isError = formState.placeError.isNotBlank(),
                        errorMessage = formState.placeError
                    )
                }
            }

            item {
                DealerFormSection(title = "Contact Information") {
                    CocoBizTextField(
                        value = formState.phone,
                        onValueChange = { viewModel.updateFormField("phone", it) },
                        label = "Phone Number *",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        isError = formState.phoneError.isNotBlank(),
                        errorMessage = formState.phoneError
                    )
                    Spacer(Modifier.height(8.dp))
                    CocoBizTextField(
                        value = formState.alternatePhone,
                        onValueChange = { viewModel.updateFormField("altPhone", it) },
                        label = "Alternate Phone",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )
                    Spacer(Modifier.height(8.dp))
                    CocoBizTextField(
                        value = formState.email,
                        onValueChange = { viewModel.updateFormField("email", it) },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )
                }
            }

            item {
                DealerFormSection(title = "Address & Notes") {
                    CocoBizTextField(
                        value = formState.address,
                        onValueChange = { viewModel.updateFormField("address", it) },
                        label = "Address",
                        leadingIcon = Icons.Default.Home,
                        maxLines = 3
                    )
                    Spacer(Modifier.height(8.dp))
                    CocoBizTextField(
                        value = formState.notes,
                        onValueChange = { viewModel.updateFormField("notes", it) },
                        label = "Notes",
                        leadingIcon = Icons.Default.Notes,
                        maxLines = 4,
                        placeholder = "Any notes about this dealer..."
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = viewModel::saveDealer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !formState.isSaving
                ) {
                    Text(
                        if (formState.isSaving) "Saving..." else if (isEditMode) "Update Dealer" else "Add Dealer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DealerFormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
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
