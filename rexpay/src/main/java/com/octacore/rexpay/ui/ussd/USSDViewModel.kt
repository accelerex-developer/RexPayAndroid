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
    handle: SavedStateHandle,
) : ViewModel() {

    private val reference: String = checkNotNull(handle["reference"])

    private val _uiState = MutableStateFlow(USSDState())
    internal val uiState = _uiState.asStateFlow()

    private val _selectedBank = mutableStateOf<USSDBank?>(null)
    val selectedBank: State<USSDBank?> = _selectedBank

    private var job: Job? = null

    init {
        job?.cancel()
        job = viewModelScope.launch {
            repo.getTransaction(reference).collect { payment ->
                LogUtils.i(payment.toString())
                _uiState.update { it.copy(payment = payment) }
            }
        }
    }

    fun onBankSelected(bank: USSDBank?) {
        _selectedBank.value = bank
        if (bank != null) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                when (val res = repo.chargeUSSD(bank, reference)) {
                    is BaseResult.Success -> {
                        _uiState.update { it.copy(isLoading = false, errorMsg = null) }
                    }

                    is BaseResult.Error -> {
                        _uiState.update { it.copy(errorMsg = res.message, isLoading = false) }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    internal companion object {
        internal fun provideFactory(repo: USSDTransactionRepo): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory, AbstractSavedStateViewModelFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return USSDViewModel(repo, handle) as T
                }
            }
    }
}

internal data class USSDState(
    internal val isLoading: Boolean = false,
    internal val errorMsg: String? = null,
    internal val payment: Payment? = null,
)