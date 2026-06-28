package com.cocobiz.app.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import android.content.ContextWrapper
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocobiz.app.ui.components.CocoBizTopBar
import com.cocobiz.app.ui.components.ConfirmationDialog

private fun android.content.Context.findFragmentActivity(): FragmentActivity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun BackupRestoreScreen(
    onNavigateBack: () -> Unit,
    onResetComplete: () -> Unit = {},
    viewModel: BackupRestoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showMenu by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    // File picker for restore
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingUri = uri
            showRestoreDialog = true
        }
    }

    // Created on-demand — traverses ContextWrapper chain to find the real FragmentActivity.
    // Must NOT be called while a Compose AlertDialog is still visible — Android silently
    // ignores BiometricPrompt.authenticate() when another window/dialog is on screen.
    fun launchBiometric() {
        try {
            val activity = context.findFragmentActivity()
            if (activity == null) {
                viewModel.showAuthError("Cannot launch authentication — please try again")
                return
            }
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm Reset")
                .setSubtitle("Authenticate to delete all app data")
                .setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
                .build()
            BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        viewModel.resetAllData()
                    }
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                            errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                        ) {
                            viewModel.showAuthError("Authentication error: $errString")
                        }
                    }
                    override fun onAuthenticationFailed() {}
                }
            ).authenticate(promptInfo)
        } catch (e: Exception) {
            viewModel.showAuthError("Authentication failed: ${e.message}")
        }
    }

    // Navigate to dashboard root after successful reset — forces all ViewModels to restart fresh
    LaunchedEffect(uiState.resetComplete) {
        if (uiState.resetComplete) {
            snackbarHostState.showSnackbar("All data deleted successfully")
            onResetComplete()
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Restore confirmation dialog
    if (showRestoreDialog) {
        ConfirmationDialog(
            title = "Restore Data?",
            message = "All current sales, dealers, and profile data will be replaced with the backup. This cannot be undone.",
            onConfirm = {
                pendingUri?.let { viewModel.restore(it) }
                pendingUri = null
                showRestoreDialog = false
            },
            onDismiss = {
                pendingUri = null
                showRestoreDialog = false
            },
            confirmText = "Restore",
            confirmButtonColor = MaterialTheme.colorScheme.error
        )
    }

    // Reset — first confirm dialog, then biometric
    if (showResetConfirmDialog) {
        ConfirmationDialog(
            title = "Reset All Data?",
            message = "This will permanently delete all sales records, dealers, and profile data. Backup files in Downloads/CocoBiz are NOT deleted.\n\nYou will need to authenticate with your device password to proceed.",
            onConfirm = {
                showResetConfirmDialog = false
                // Launch in scope so delay() runs after this frame completes — direct
                // BiometricPrompt.authenticate() call while a dialog is still animating
                // out is silently ignored by the system on all Android versions.
                coroutineScope.launch {
                    delay(300)
                    launchBiometric()
                }
            },
            onDismiss = { showResetConfirmDialog = false },
            confirmText = "Continue",
            confirmButtonColor = MaterialTheme.colorScheme.error
        )
    }

    Scaffold(
        topBar = {
            CocoBizTopBar(
                title = "Backup & Restore",
                onNavigateBack = onNavigateBack,
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.DeleteForever,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            "Reset All Data",
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    showResetConfirmDialog = true
                                }
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Backup card ───────────────────────────────────────────
            item {
                ActionCard(
                    icon = Icons.Default.Backup,
                    iconBackground = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Create Backup",
                    description = "Exports all sales, dealers & profile data as an encrypted file to Downloads/CocoBiz.",
                    actionContent = {
                        Button(
                            onClick = { viewModel.backup() },
                            enabled = !uiState.isBackingUp && !uiState.isRestoring && !uiState.isResetting,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isBackingUp) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Creating backup…", fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(Icons.Default.Backup, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Create Backup", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                )
            }

            // ── Restore card ──────────────────────────────────────────
            item {
                ActionCard(
                    icon = Icons.Default.FolderOpen,
                    iconBackground = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    iconTint = MaterialTheme.colorScheme.error,
                    title = "Restore from Backup",
                    description = "Choose a .cocobak file from Downloads/CocoBiz to restore your data.",
                    actionContent = {
                        OutlinedButton(
                            onClick = { filePicker.launch(arrayOf("*/*")) },
                            enabled = !uiState.isBackingUp && !uiState.isRestoring && !uiState.isResetting,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            if (uiState.isRestoring) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.error,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(10.dp))
                                Text("Restoring data…", fontWeight = FontWeight.SemiBold)
                            } else {
                                Icon(Icons.Default.Restore, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Choose Backup File", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                )
            }

            // ── Info card ─────────────────────────────────────────────
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InfoRow(
                            icon = Icons.Default.Lock,
                            iconTint = MaterialTheme.colorScheme.primary,
                            text = "Backup files are AES-256 encrypted — only CocoBiz can read them."
                        )
                        InfoRow(
                            icon = Icons.Default.CheckCircle,
                            iconTint = Color(0xFF2E7D32),
                            text = "Files are saved to Downloads/CocoBiz as .cocobak files."
                        )
                        InfoRow(
                            icon = Icons.Default.Warning,
                            iconTint = Color(0xFFE65100),
                            text = "Reset (⋮ menu) permanently deletes all data and requires device authentication."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    description: String,
    actionContent: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            actionContent()
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    iconTint: Color,
    text: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
