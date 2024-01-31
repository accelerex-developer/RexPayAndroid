@file:JvmSynthetic

package com.octacore.rexpay.ui.selection

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.models.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal class SelectionViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: BasePaymentRepo
) : ViewModel() {

    private val reference: String = checkNotNull(savedStateHandle["reference"])

    private val _uiState = MutableStateFlow(SelectionState())
    internal val uiState = _uiState.asStateFlow()

    init {
        initiateTransaction()
    }

    internal fun initiateTransaction() {
        _uiState.update { SelectionState(isLoading = true) }
        viewModelScope.launch {
            when (val res = repo.initiatePayment(reference)) {
                is BaseResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, response = res.result) }
                }

                is BaseResult.Error -> {
                    _uiState.update { it.copy(errorMsg = res.message, isLoading = false) }
                }
            }
        }
    }

    internal fun dismissError() {
        _uiState.update { SelectionState() }
    }

    internal companion object {
        internal fun provideFactory(repo: BasePaymentRepo): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory, AbstractSavedStateViewModelFactory() {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return SelectionViewModel(handle, repo) as T
                }
            }
    }
}

internal data class SelectionState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val response: Payment? = null,
)
