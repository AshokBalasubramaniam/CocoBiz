package com.cocobiz.app.presentation.profile

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.R
import com.cocobiz.app.ui.components.CocoBizTopBar
import com.cocobiz.app.ui.components.FormInput
import com.cocobiz.app.ui.components.PrimaryButton
import com.cocobiz.app.ui.components.SectionCard
import com.cocobiz.app.ui.theme.DashboardColors
import com.cocobiz.app.ui.theme.FigmaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.savedEventCount) {
        if (uiState.savedEventCount > 0) {
            Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            viewModel.toggleEditing()
        }
    }

    Scaffold(
        topBar = {
            CocoBizTopBar(
                title = "Business Profile",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (!uiState.isEditing) {
                        IconButton(onClick = viewModel::toggleEditing) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = FigmaColors.Blue600)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(DashboardColors.BannerGradientStart, DashboardColors.BannerGradientEnd)
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
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FigmaColors.White,
                            textAlign = TextAlign.Center
                        )
                        if (uiState.ownerName.isNotBlank()) {
                            Text(
                                text = uiState.ownerName,
                                fontSize = 13.sp,
                                color = FigmaColors.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            if (uiState.isEditing) {
                item {
                    SectionCard(
                        icon = { Icon(Icons.Default.Business, contentDescription = null, tint = FigmaColors.Blue600, modifier = Modifier.size(18.dp)) },
                        iconBackground = FigmaColors.Blue100,
                        title = "Business Information",
                        titleColor = FigmaColors.Blue700,
                        subtitle = "Your business identity",
                        subtitleColor = FigmaColors.Blue400,
                        bannerColor = FigmaColors.Blue50,
                        borderColor = FigmaColors.Blue100,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        FormInput(
                            value = uiState.businessName,
                            onValueChange = { viewModel.updateField("businessName", it) },
                            placeholder = "Business Name *",
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                            errorMessage = uiState.businessNameError
                        )
                        FormInput(
                            value = uiState.ownerName,
                            onValueChange = { viewModel.updateField("ownerName", it) },
                            placeholder = "Owner Name *",
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                            errorMessage = uiState.ownerNameError
                        )
                        FormInput(
                            value = uiState.gstNumber,
                            onValueChange = { viewModel.updateField("gstNumber", it) },
                            placeholder = "GST Number (Optional)",
                            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                item {
                    SectionCard(
                        icon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Green700, modifier = Modifier.size(18.dp)) },
                        iconBackground = FigmaColors.Green200,
                        title = "Contact Information",
                        titleColor = FigmaColors.Green800,
                        subtitle = "How dealers and staff reach you",
                        subtitleColor = FigmaColors.Green600,
                        bannerColor = FigmaColors.Green100,
                        borderColor = FigmaColors.Green200,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        FormInput(
                            value = uiState.phone,
                            onValueChange = { viewModel.updateField("phone", it) },
                            placeholder = "Mobile Number *",
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            errorMessage = uiState.phoneError
                        )
                        FormInput(
                            value = uiState.alternatePhone,
                            onValueChange = { viewModel.updateField("alternatePhone", it) },
                            placeholder = "Alternate Number",
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        FormInput(
                            value = uiState.email,
                            onValueChange = { viewModel.updateField("email", it) },
                            placeholder = "Email",
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                    }
                }

                item {
                    SectionCard(
                        icon = { Icon(Icons.Default.Home, contentDescription = null, tint = FigmaColors.Orange600, modifier = Modifier.size(18.dp)) },
                        iconBackground = FigmaColors.Orange100,
                        title = "Address",
                        titleColor = FigmaColors.Orange700,
                        subtitle = "Where your business is located",
                        subtitleColor = FigmaColors.Orange400,
                        bannerColor = FigmaColors.Orange50,
                        borderColor = FigmaColors.Orange100,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        FormInput(
                            value = uiState.address,
                            onValueChange = { viewModel.updateField("address", it) },
                            placeholder = "Full Address",
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FormInput(
                                value = uiState.city,
                                onValueChange = { viewModel.updateField("city", it) },
                                placeholder = "City",
                                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                            FormInput(
                                value = uiState.state,
                                onValueChange = { viewModel.updateField("state", it) },
                                placeholder = "State",
                                leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        FormInput(
                            value = uiState.pincode,
                            onValueChange = { viewModel.updateField("pincode", it) },
                            placeholder = "Pincode",
                            leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = viewModel::toggleEditing,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Bold)
                        }
                        PrimaryButton(
                            text = if (uiState.isSaving) "Saving..." else "Save",
                            onClick = viewModel::saveProfile,
                            enabled = !uiState.isSaving,
                            modifier = Modifier.weight(1f),
                            icon = { Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            } else {
                item {
                    SectionCard(
                        icon = { Icon(Icons.Default.Business, contentDescription = null, tint = FigmaColors.Blue600, modifier = Modifier.size(18.dp)) },
                        iconBackground = FigmaColors.Blue100,
                        title = "Business Information",
                        titleColor = FigmaColors.Blue700,
                        subtitle = "Your business identity",
                        subtitleColor = FigmaColors.Blue400,
                        bannerColor = FigmaColors.Blue50,
                        borderColor = FigmaColors.Blue100,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        ProfileInfoRow(Icons.Default.Business, "Business Name", uiState.businessName)
                        ProfileInfoRow(Icons.Default.Person, "Owner Name", uiState.ownerName)
                        if (uiState.gstNumber.isNotBlank()) {
                            ProfileInfoRow(Icons.Default.Receipt, "GST Number", uiState.gstNumber)
                        }
                    }
                }

                item {
                    SectionCard(
                        icon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Green700, modifier = Modifier.size(18.dp)) },
                        iconBackground = FigmaColors.Green200,
                        title = "Contact Information",
                        titleColor = FigmaColors.Green800,
                        subtitle = "How dealers and staff reach you",
                        subtitleColor = FigmaColors.Green600,
                        bannerColor = FigmaColors.Green100,
                        borderColor = FigmaColors.Green200,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        ProfileInfoRow(Icons.Default.Phone, "Mobile", uiState.phone)
                        if (uiState.alternatePhone.isNotBlank()) {
                            ProfileInfoRow(Icons.Default.Phone, "Alternate", uiState.alternatePhone)
                        }
                        if (uiState.email.isNotBlank()) {
                            ProfileInfoRow(Icons.Default.Email, "Email", uiState.email)
                        }
                    }
                }

                if (uiState.address.isNotBlank() || uiState.city.isNotBlank()) {
                    item {
                        SectionCard(
                            icon = { Icon(Icons.Default.Home, contentDescription = null, tint = FigmaColors.Orange600, modifier = Modifier.size(18.dp)) },
                            iconBackground = FigmaColors.Orange100,
                            title = "Address",
                            titleColor = FigmaColors.Orange700,
                            subtitle = "Where your business is located",
                            subtitleColor = FigmaColors.Orange400,
                            bannerColor = FigmaColors.Orange50,
                            borderColor = FigmaColors.Orange100,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            if (uiState.address.isNotBlank()) ProfileInfoRow(Icons.Default.Home, "Address", uiState.address)
                            if (uiState.city.isNotBlank()) ProfileInfoRow(Icons.Default.LocationCity, "City", uiState.city)
                            if (uiState.state.isNotBlank()) ProfileInfoRow(Icons.Default.Map, "State", uiState.state)
                            if (uiState.pincode.isNotBlank()) ProfileInfoRow(Icons.Default.Pin, "Pincode", uiState.pincode)
                        }
                    }
                }

                item {
                    PrimaryButton(
                        text = "Edit Profile",
                        onClick = viewModel::toggleEditing,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        icon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FigmaColors.Gray100),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = FigmaColors.Gray600, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(label, fontSize = 11.sp, color = FigmaColors.Gray400)
            Text(value.ifBlank { "—" }, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = FigmaColors.Gray900)
        }
    }
}
