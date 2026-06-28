package com.cocobiz.app.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Receipt
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.cocobiz.app.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.ui.components.CocoBizTextField
import com.cocobiz.app.ui.components.CocoBizTopBar
import com.cocobiz.app.ui.theme.GradientGreenEnd
import com.cocobiz.app.ui.theme.GradientGreenStart

@Composable
fun BusinessProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // isSaved resets immediately when the repository emits the freshly-saved profile,
    // which cancels any snackbar coroutine. Toast is system-level and survives the reset.
    LaunchedEffect(uiState.savedEventCount) {
        if (uiState.savedEventCount > 0) {
            Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CocoBizTopBar(
                title = "Business Profile",
                onNavigateBack = onNavigateBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Profile header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(GradientGreenStart, GradientGreenEnd)
                            )
                        )
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(R.drawable.ic_app_logo),
                            contentDescription = "CocoBiz Logo",
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(22.dp)),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = uiState.businessName.ifBlank { "Your Business" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        if (uiState.ownerName.isNotBlank()) {
                            Text(
                                text = uiState.ownerName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Business Information
            item {
                ProfileSection(title = "Business Information") {
                    CocoBizTextField(
                        value = uiState.businessName,
                        onValueChange = { viewModel.updateField("businessName", it) },
                        label = "Business Name *",
                        leadingIcon = Icons.Default.Business,
                        isError = uiState.businessNameError.isNotBlank(),
                        errorMessage = uiState.businessNameError
                    )
                    Spacer(Modifier.height(12.dp))
                    CocoBizTextField(
                        value = uiState.ownerName,
                        onValueChange = { viewModel.updateField("ownerName", it) },
                        label = "Owner Name *",
                        leadingIcon = Icons.Default.Person,
                        isError = uiState.ownerNameError.isNotBlank(),
                        errorMessage = uiState.ownerNameError
                    )
                    Spacer(Modifier.height(12.dp))
                    CocoBizTextField(
                        value = uiState.gstNumber,
                        onValueChange = { viewModel.updateField("gstNumber", it) },
                        label = "GST Number (Optional)",
                        leadingIcon = Icons.Default.Receipt
                    )
                }
            }

            // Contact Details
            item {
                ProfileSection(title = "Contact Information") {
                    CocoBizTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.updateField("phone", it) },
                        label = "Mobile Number *",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        isError = uiState.phoneError.isNotBlank(),
                        errorMessage = uiState.phoneError
                    )
                    Spacer(Modifier.height(12.dp))
                    CocoBizTextField(
                        value = uiState.alternatePhone,
                        onValueChange = { viewModel.updateField("alternatePhone", it) },
                        label = "Alternate Number",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )
                    Spacer(Modifier.height(12.dp))
                    CocoBizTextField(
                        value = uiState.email,
                        onValueChange = { viewModel.updateField("email", it) },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )
                }
            }

            // Address
            item {
                ProfileSection(title = "Address & Notes") {
                    CocoBizTextField(
                        value = uiState.address,
                        onValueChange = { viewModel.updateField("address", it) },
                        label = "Full Address",
                        leadingIcon = Icons.Default.Home,
                        maxLines = 3
                    )
                    Spacer(Modifier.height(12.dp))
                    CocoBizTextField(
                        value = uiState.city,
                        onValueChange = { viewModel.updateField("city", it) },
                        label = "City",
                        leadingIcon = Icons.Default.LocationCity
                    )
                    Spacer(Modifier.height(12.dp))
                    CocoBizTextField(
                        value = uiState.state,
                        onValueChange = { viewModel.updateField("state", it) },
                        label = "State",
                        leadingIcon = Icons.Default.Map
                    )
                    Spacer(Modifier.height(12.dp))
                    CocoBizTextField(
                        value = uiState.pincode,
                        onValueChange = { viewModel.updateField("pincode", it) },
                        label = "Pincode",
                        leadingIcon = Icons.Default.PinDrop,
                        keyboardType = KeyboardType.Number
                    )
                }
            }

            // Save button
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = viewModel::saveProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (uiState.isSaving) "Saving..." else "Save Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}
