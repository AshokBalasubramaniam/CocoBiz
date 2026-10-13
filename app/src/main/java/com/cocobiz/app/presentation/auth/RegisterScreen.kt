package com.cocobiz.app.presentation.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.ui.components.CocoBizTopBar
import com.cocobiz.app.ui.components.FormInput
import com.cocobiz.app.ui.components.PrimaryButton
import com.cocobiz.app.ui.components.SectionCard
import com.cocobiz.app.ui.theme.FigmaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    if (state.isSuccess) {
        AlertDialog(
            onDismissRequest = { onRegisterSuccess() },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FigmaColors.Green600, modifier = Modifier.size(48.dp)) },
            title = { Text("Account Created!", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            text = { Text("Your account has been created successfully. Please sign in with your new credentials.", fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = { onRegisterSuccess() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign In", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        topBar = {
            CocoBizTopBar(
                title = "Create Account",
                onNavigateBack = onNavigateToLogin
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
                    icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FigmaColors.Blue600, modifier = Modifier.size(18.dp)) },
                    iconBackground = FigmaColors.Blue100,
                    title = "Account Details",
                    titleColor = FigmaColors.Blue700,
                    subtitle = "Choose your login credentials",
                    subtitleColor = FigmaColors.Blue400,
                    bannerColor = FigmaColors.Blue50,
                    borderColor = FigmaColors.Blue100
                ) {
                    FormInput(
                        value = state.username,
                        onValueChange = viewModel::onUsernameChange,
                        placeholder = "Username *",
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Text("Lowercase letters only, min 3 characters", fontSize = 11.sp, color = FigmaColors.Gray400)

                    FormInput(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        placeholder = "Password *",
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            Icon(
                                if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = FigmaColors.Gray400,
                                modifier = Modifier.size(18.dp).clickable { passwordVisible = !passwordVisible }
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                    )
                    Text("Minimum 6 characters", fontSize = 11.sp, color = FigmaColors.Gray400)

                    FormInput(
                        value = state.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        placeholder = "Confirm Password *",
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            Icon(
                                if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (confirmVisible) "Hide password" else "Show password",
                                tint = FigmaColors.Gray400,
                                modifier = Modifier.size(18.dp).clickable { confirmVisible = !confirmVisible }
                            )
                        },
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
                    )
                }
            }

            item {
                SectionCard(
                    icon = { Icon(Icons.Default.Business, contentDescription = null, tint = FigmaColors.Green700, modifier = Modifier.size(18.dp)) },
                    iconBackground = FigmaColors.Green200,
                    title = "Business Details",
                    titleColor = FigmaColors.Green800,
                    subtitle = "Tell us about your business",
                    subtitleColor = FigmaColors.Green600,
                    bannerColor = FigmaColors.Green100,
                    borderColor = FigmaColors.Green200
                ) {
                    FormInput(
                        value = state.businessName,
                        onValueChange = viewModel::onBusinessNameChange,
                        placeholder = "Business Name *",
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    FormInput(
                        value = state.ownerName,
                        onValueChange = viewModel::onOwnerNameChange,
                        placeholder = "Owner Name",
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    FormInput(
                        value = state.phone,
                        onValueChange = viewModel::onPhoneChange,
                        placeholder = "Phone *",
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                    )
                    FormInput(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = "Email (for reminders)",
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FigmaColors.Gray400, modifier = Modifier.size(18.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
                    )
                }
            }

            if (state.error != null) {
                item {
                    Text(state.error!!, color = FigmaColors.Red600, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
                }
            }

            item {
                PrimaryButton(
                    text = if (state.isLoading) "Creating account..." else "Create Account",
                    onClick = { viewModel.register() },
                    enabled = !state.isLoading,
                    icon = {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = FigmaColors.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", fontSize = 13.sp, color = FigmaColors.Gray500)
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Sign In", fontWeight = FontWeight.SemiBold, color = FigmaColors.Blue600, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
