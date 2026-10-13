package com.cocobiz.app.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.R
import com.cocobiz.app.ui.components.FormInput
import com.cocobiz.app.ui.theme.DashboardColors
import com.cocobiz.app.ui.theme.FigmaColors

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaColors.White)
    ) {
        Image(
            painter = painterResource(R.drawable.bg_login_top),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Welcome Back!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = FigmaColors.Gray900)
                Text("Sign in to continue to CocoBiz", fontSize = 13.sp, color = FigmaColors.Gray500)
            }

            Spacer(Modifier.height(4.dp))

            FormInput(
                value = state.username,
                onValueChange = viewModel::onUsernameChange,
                placeholder = "Username",
                leadingIcon = { RoundIconBadge(Icons.Default.Person) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
            )

            FormInput(
                value = state.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "Password",
                leadingIcon = { RoundIconBadge(Icons.Default.Lock) },
                trailingIcon = {
                    Icon(
                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = FigmaColors.Gray400,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { passwordVisible = !passwordVisible }
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )

            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = FigmaColors.Green600, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            if (state.error != null) {
                Text(state.error!!, color = FigmaColors.Red600, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
            }

            GreenGradientButton(
                text = if (state.isLoading) "Signing in..." else "Login",
                onClick = { viewModel.login(onLoginSuccess) },
                enabled = !state.isLoading,
                icon = {
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = FigmaColors.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = FigmaColors.White, modifier = Modifier.size(18.dp))
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?", fontSize = 13.sp, color = FigmaColors.Gray500)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Create Account", fontWeight = FontWeight.Bold, color = FigmaColors.Green600, fontSize = 13.sp)
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.bg_login_bottom),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
private fun RoundIconBadge(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(FigmaColors.Green100),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = FigmaColors.Green800, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun GreenGradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(DashboardColors.BannerGradientStart, DashboardColors.BannerGradientEnd)))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            icon()
            Text(text, color = FigmaColors.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
