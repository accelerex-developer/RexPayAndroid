@file:JvmSynthetic

package com.octacore.rexpay.ui.banktransfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.data.remote.models.ChargeBankResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.repo.BankTransactionRepo
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

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

    internal fun initiate() {
        _uiState.update { BankTransferState(isLoading = true) }
        viewModelScope.launch {
            val payment = async { createPayment() }.await()
            val detail = async { chargeBank(payment) }.await()
            _uiState.update { it.copy(account = detail) }
        }
    }

    private suspend fun createPayment(): PaymentCreationResponse? {
        return when (val res = baseRepo.initiatePayment()) {
            is BaseResult.Error -> {
                _uiState.update { it.copy(errorMsg = res) }
                null
            }
            is BaseResult.Success -> res.result
        }
    }

    private suspend fun chargeBank(payment: PaymentCreationResponse?): ChargeBankResponse? {
        return when (val res = repo.initiateBankTransfer(payment)) {
            is BaseResult.Error -> {
                _uiState.update { it.copy(errorMsg = res) }
                null
            }
            is BaseResult.Success -> res.result
        }
    }

    internal fun reset() = _uiState.update { BankTransferState() }

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
    internal val account: ChargeBankResponse? = null
)