@file:JvmSynthetic

package com.octacore.rexpay.ui.ussd

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.remote.models.ChargeUssdResponse
import com.octacore.rexpay.data.remote.models.PaymentCreationResponse
import com.octacore.rexpay.domain.models.USSDBank
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import com.octacore.rexpay.domain.repo.USSDTransactionRepo
import kotlinx.coroutines.async
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
internal class USSDViewModel(
    private val repo: USSDTransactionRepo,
    private val baseRepo: BasePaymentRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(USSDUiState())
    internal val uiState = _uiState.asStateFlow()

    private val _selectedBank = mutableStateOf<USSDBank?>(null)
    val selectedBank: State<USSDBank?> = _selectedBank

    private var _payment: PaymentCreationResponse? = null

    internal fun onBankSelected(bank: USSDBank?) {
        _selectedBank.value = bank
        if (bank != null) {
            _uiState.update { USSDUiState(isLoading = true) }
            viewModelScope.launch {
                val payment = async { createPayment() }.await()
                _payment = payment
                val response = async { chargeUSSD(payment, bank) }.await()
                _uiState.update { USSDUiState(response = response) }
            }
        } else {
            _uiState.update { USSDUiState() }
        }
    }

    internal fun checkTransactionStatus(reference: String?) {
        viewModelScope.launch {
            val clientId = _payment?.clientId
            async { repo.checkTransactionStatus(reference, clientId) }.await()
        }
    }

    private suspend fun createPayment(): PaymentCreationResponse? {
        return when (val res = baseRepo.initiatePayment()) {
            is BaseResult.Error -> {
                _uiState.update { USSDUiState(errorMsg = res) }
                null
            }

            is BaseResult.Success -> res.result
        }
    }

    private suspend fun chargeUSSD(
        payment: PaymentCreationResponse?,
        bank: USSDBank?
    ): ChargeUssdResponse? {
        return when (val res = repo.chargeUSSD(payment, bank)) {
            is BaseResult.Error -> {
                _uiState.update { USSDUiState(errorMsg = res) }
                null
            }

            is BaseResult.Success -> res.result
        }
    }

    internal companion object {
        internal fun provideFactory(
            repo: USSDTransactionRepo,
            baseRepo: BasePaymentRepo
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return USSDViewModel(repo, baseRepo) as T
                }
            }
    }
}

internal data class USSDUiState(
    internal val isLoading: Boolean = false,
    internal val response: ChargeUssdResponse? = null,
    internal val errorMsg: BaseResult.Error? = null,
)