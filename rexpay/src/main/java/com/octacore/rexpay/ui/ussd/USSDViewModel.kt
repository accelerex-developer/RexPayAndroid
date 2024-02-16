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
import com.octacore.rexpay.data.remote.models.UssdPaymentDetailResponse
import com.octacore.rexpay.domain.models.USSDBank
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import com.octacore.rexpay.domain.repo.USSDTransactionRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
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

    private val _chargeUssdState = MutableStateFlow(ChargeUSSDState())
    internal val chargeUSSDState = _chargeUssdState.asStateFlow()

    private val _verifyUssdState = MutableStateFlow(VerifyUSSDState())
    internal val verifyUSSDState = _verifyUssdState.asStateFlow()

    private val _paymentCreationRes = mutableStateOf<PaymentCreationResponse?>(null)
    private val _selectedBank = mutableStateOf<USSDBank?>(null)
    internal val selectedBank: State<USSDBank?> = _selectedBank

    private var _networkJob: Job? = null

    internal fun onBankSelected(bank: USSDBank?) {
        val previous = _selectedBank.value
        _selectedBank.value = bank
        if (previous?.code == bank?.code) {
            _selectedBank.value = bank
        } else if (bank != null) {
            viewModelScope.launch {
                val success = chargeUSSD(bank)
                if (success.not()) {
                    _selectedBank.value = previous
                }
            }
        } else {
            _selectedBank.value = null
            _chargeUssdState.update { ChargeUSSDState() }
            _verifyUssdState.update { VerifyUSSDState() }
        }
    }

    internal fun checkTransactionStatus(reference: String?) {
        _networkJob?.cancel()
        _verifyUssdState.update { VerifyUSSDState(isLoading = true) }
        viewModelScope.launch {
            when (val res = repo.checkTransactionStatus(reference)) {
                is BaseResult.Error -> _verifyUssdState.update { VerifyUSSDState(errorMsg = res) }
                is BaseResult.Success -> _verifyUssdState.update { VerifyUSSDState(response = res.result) }
            }
        }
    }

    internal fun reset(type: Int) {
        when (type) {
            1 -> _chargeUssdState.update { it.copy(errorMsg = null) }
            2 -> _verifyUssdState.update { VerifyUSSDState() }
        }
        _chargeUssdState.update { it.copy(isLoading = false) }
        _verifyUssdState.update { it.copy(isLoading = false) }
    }

    private suspend fun chargeUSSD(bank: USSDBank?): Boolean {
        _networkJob?.join()
        _chargeUssdState.update { it.copy(isLoading = true) }
        val payload = _paymentCreationRes.value
        if (payload != null) {
            return when (val res = repo.chargeUSSD(payload, bank)) {
                is BaseResult.Error -> {
                    _chargeUssdState.update { it.copy(errorMsg = res) }
                    false
                }

                is BaseResult.Success -> {
                    _chargeUssdState.update { it.copy(response = res.result) }
                    true
                }
            }.also {
                _chargeUssdState.update { it.copy(isLoading = false) }
            }
        } else {
            initPayment()
            return chargeUSSD(bank)
        }
    }

    private suspend fun initPayment() {
        _networkJob?.join()
        _chargeUssdState.update { it.copy(isLoading = true) }
        when (val paymentRes = baseRepo.initiatePayment()) {
            is BaseResult.Error -> _chargeUssdState.update { ChargeUSSDState(errorMsg = paymentRes) }
            is BaseResult.Success -> _paymentCreationRes.value = paymentRes.result
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

internal data class ChargeUSSDState(
    internal val isLoading: Boolean = false,
    internal val response: ChargeUssdResponse? = null,
    internal val errorMsg: BaseResult.Error? = null,
)

internal data class VerifyUSSDState(
    internal val isLoading: Boolean = false,
    internal val response: UssdPaymentDetailResponse? = null,
    internal val errorMsg: BaseResult.Error? = null,
)