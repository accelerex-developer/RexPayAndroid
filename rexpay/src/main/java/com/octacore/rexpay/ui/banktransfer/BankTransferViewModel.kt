@file:JvmSynthetic

package com.octacore.rexpay.ui.banktransfer

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.repo.BankTransactionRepo
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import com.octacore.rexpay.utils.LogUtils
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
 * Date            : 30/01/2024
 **************************************************************************************************/
internal class BankTransferViewModel(
    private val repo: BankTransactionRepo,
    private val baseRepo: BasePaymentRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BankTransferState())
    internal val uiState = _uiState.asStateFlow()

    private var job: Job? = null

    init {
        job?.cancel()
        /*job = viewModelScope.launch {
            baseRepo.getTransaction(reference).collect { payment ->
                LogUtils.i(payment.toString())
                _uiState.update { it.copy(payment = payment) }
            }
        }*/
    }

    internal fun initiate() {
        _uiState.update { BankTransferState(isLoading = true, payment = it.payment) }
        viewModelScope.launch {
            when (val res = repo.initiateBankTransfer()) {
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
                        account = res.result
                    )
                }
            }
        }
    }

    internal fun reset() = _uiState.update { BankTransferState(payment = it.payment) }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    internal companion object {
        internal fun provideFactory(
            repo: BankTransactionRepo,
            baseRepo: BasePaymentRepo,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BankTransferViewModel(repo, baseRepo) as T
                }
            }
    }
}

internal data class BankTransferState(
    internal val isLoading: Boolean = false,
    internal val errorMsg: BaseResult.Error? = null,
    internal val payment: Payment? = null,
    internal val account: BankAccount? = null
)