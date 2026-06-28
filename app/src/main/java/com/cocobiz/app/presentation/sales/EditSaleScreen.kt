package com.cocobiz.app.presentation.sales

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditSaleScreen(
    saleId: Long,
    onNavigateBack: () -> Unit,
    viewModel: AddSaleViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()

    LaunchedEffect(saleId) {
        viewModel.loadSaleForEdit(saleId)
    }

    // Navigation is handled by AddSaleScreen's own LaunchedEffect — no duplicate here.

    AddSaleScreen(
        onNavigateBack = onNavigateBack,
        viewModel = viewModel
    )
}
