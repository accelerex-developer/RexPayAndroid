@file:JvmSynthetic

package com.octacore.rexpay.ui.selection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.domain.BasePaymentRepo
import com.octacore.rexpay.models.PayPayload
import com.octacore.rexpay.models.PaymentCreationResponse
import com.octacore.rexpay.utils.LogUtils
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
    private val repo: BasePaymentRepo,
    private val payload: PayPayload?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectionState())
    val uiState = _uiState.asStateFlow()

    init {
        initiateTransaction()
    }

    fun initiateTransaction() {
        _uiState.update { SelectionState(isLoading = true) }
        viewModelScope.launch {
            when (val res = repo.initiatePayment(payload)) {
                is BaseResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, response = res.result) }
                }

                is BaseResult.Error -> {
                    _uiState.update { it.copy(errorMsg = res.message, isLoading = false) }
                }
            }
        }
    }

    fun dismissError() {
        _uiState.update { SelectionState() }
    }

    companion object {
        fun provideFactory(repo: BasePaymentRepo, payload: PayPayload?): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SelectionViewModel(repo, payload) as T
                }
            }
    }
}

internal data class SelectionState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val response: PaymentCreationResponse? = null,
)
