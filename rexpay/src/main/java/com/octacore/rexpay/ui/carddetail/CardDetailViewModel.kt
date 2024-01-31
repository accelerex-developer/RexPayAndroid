@file:JvmSynthetic

package com.octacore.rexpay.ui.carddetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.domain.models.Payment
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.domain.repo.USSDTransactionRepo
import com.octacore.rexpay.ui.ussd.USSDState
import com.octacore.rexpay.ui.ussd.USSDViewModel
import com.octacore.rexpay.utils.CreditCardFormatter
import com.octacore.rexpay.utils.ExpiryDateFormatter
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
 * Date            : 18/01/2024
 **************************************************************************************************/

internal class CardDetailViewModel(
    private val repo: CardTransactionRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val reference: String = checkNotNull(savedStateHandle["reference"])

    private val _uiState = MutableStateFlow(CardDetailUiState())
    internal val uiState = _uiState.asStateFlow()

    var cardholder by mutableStateOf(CreditCardFormatter())
    var expiryDate by mutableStateOf(ExpiryDateFormatter())
    var cvv by mutableStateOf("")
    var pin by mutableStateOf("")

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

    internal companion object {
        internal fun provideFactory(repo: CardTransactionRepo): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory, AbstractSavedStateViewModelFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return CardDetailViewModel(repo, handle) as T
                }
            }
    }
}

internal data class CardDetailUiState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val payment: Payment? = null,
)