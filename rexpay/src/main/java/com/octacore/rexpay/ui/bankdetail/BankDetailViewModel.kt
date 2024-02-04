@file:JvmSynthetic

package com.octacore.rexpay.ui.bankdetail

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.data.remote.models.TransactionStatusResponse
import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.repo.BankTransactionRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 01/02/2024
 **************************************************************************************************/
internal class BankDetailViewModel(private val repo: BankTransactionRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(BankDetailUiState())
    internal val uiState = _uiState.asStateFlow()

    internal fun confirmTransaction(reference: String?) {
        _uiState.update { BankDetailUiState(isLoading = true) }
        viewModelScope.launch {
            when (val res = repo.checkTransactionStatus(reference)) {
                is BaseResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = res
                    )
                }

                is BaseResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMsg = null,
                        response = res.result
                    )
                }
            }
        }
    }

    fun reset() = _uiState.update { BankDetailUiState() }

    internal companion object {
        internal fun provideFactory(repo: BankTransactionRepo): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BankDetailViewModel(repo) as T
                }
            }
    }
}

internal data class BankDetailUiState(
    internal val isLoading: Boolean = false,
    internal val errorMsg: BaseResult.Error? = null,
    internal val response: TransactionStatusResponse? = null
)