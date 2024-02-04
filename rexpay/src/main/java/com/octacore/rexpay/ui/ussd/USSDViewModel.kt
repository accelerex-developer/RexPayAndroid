@file:JvmSynthetic

package com.octacore.rexpay.ui.ussd

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.domain.models.BaseResult
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.repo.USSDTransactionRepo
import com.octacore.rexpay.domain.models.USSDBank
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
internal class USSDViewModel(
    private val repo: USSDTransactionRepo,
    baseRepo: BasePaymentRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(USSDUiState())
    internal val uiState = _uiState.asStateFlow()

    private val _selectedBank = mutableStateOf<USSDBank?>(null)
    val selectedBank: State<USSDBank?> = _selectedBank

    private var job: Job? = null

    init {
        job?.cancel()
        /*job = viewModelScope.launch {
            baseRepo.getTransaction(reference).collect { payment ->
                LogUtils.i("USSDViewModel: $payment")
                _uiState.update { it.copy(payment = payment) }
            }
        }*/
    }

    fun onBankSelected(bank: USSDBank?) {
        _selectedBank.value = bank
        if (bank != null) {
            _uiState.update { it.copy(isLoading = true, errorMsg = null, code = null) }
            viewModelScope.launch {
                when (val res = repo.chargeUSSD(bank)) {
                    is BaseResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMsg = null,
                                code = res.result
                            )
                        }
                    }

                    is BaseResult.Error -> {
                        _uiState.update {
                            it.copy(
                                errorMsg = res,
                                isLoading = false,
                                code = null
                            )
                        }
                    }
                }
            }
        } else {
            _uiState.update { USSDUiState(payment = it.payment) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
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
    internal val code: String? = null,
    internal val errorMsg: BaseResult.Error? = null,
    internal val payment: Payment? = null,
)