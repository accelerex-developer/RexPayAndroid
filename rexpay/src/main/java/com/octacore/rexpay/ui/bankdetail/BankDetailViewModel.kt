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
internal class BankDetailViewModel(
    private val repo: BankTransactionRepo,
    handle: SavedStateHandle
) : ViewModel() {

    private val reference: String = checkNotNull(handle["reference"])

    private val _uiState = MutableStateFlow(BankDetailUiState())
    internal val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    init {
        job?.cancel()
        job = viewModelScope.launch {
            repo.getAccount(reference).collect { account ->
                _uiState.update { it.copy(account = account) }
            }
        }
    }

    fun confirmTransaction() {
        _uiState.update { BankDetailUiState(isLoading = true, account = it.account) }
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

    fun reset() = _uiState.update { BankDetailUiState(account = it.account) }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    internal companion object {
        internal fun provideFactory(repo: BankTransactionRepo): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory, AbstractSavedStateViewModelFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return BankDetailViewModel(repo, handle) as T
                }
            }
    }
}

internal data class BankDetailUiState(
    internal val isLoading: Boolean = false,
    internal val errorMsg: BaseResult.Error? = null,
    internal val account: BankAccount? = null,
    internal val response: TransactionStatusResponse? = null
)