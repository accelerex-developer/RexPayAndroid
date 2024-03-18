@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.banktransfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.globalaccelerex.rexpay.data.remote.models.ChargeBankResponse
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.domain.repo.BankTransactionRepo
import com.globalaccelerex.rexpay.domain.repo.BasePaymentRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class BankTransferViewModel(
    private val repo: BankTransactionRepo,
    private val baseRepo: BasePaymentRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BankTransferState())
    internal val uiState = _uiState.asStateFlow()

    internal fun initiate() {
        _uiState.update { BankTransferState(isLoading = true) }
        viewModelScope.launch {
            when (val payRes = baseRepo.initiatePayment()) {
                is BaseResult.Error -> _uiState.update { BankTransferState(errorMsg = payRes) }
                is BaseResult.Success -> {
                    when (val res = repo.initiateBankTransfer(payRes.result)) {
                        is BaseResult.Error -> _uiState.update { BankTransferState(errorMsg = res) }
                        is BaseResult.Success -> _uiState.update { BankTransferState(account = res.result) }
                    }
                }
            }
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