@file:JvmSynthetic

package com.octacore.rexpay.ui.carddetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.utils.CreditCardFormatter
import com.octacore.rexpay.utils.ExpiryDateFormatter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 18/01/2024
 **************************************************************************************************/

internal class CardDetailViewModel(
    private val repo: CardTransactionRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardDetailUiState())
    internal val uiState = _uiState.asStateFlow()

    var cardholder by mutableStateOf(CreditCardFormatter())
    var expiryDate by mutableStateOf(ExpiryDateFormatter())
    var cvv by mutableStateOf("")
    var pin by mutableStateOf("")

    private var job: Job? = null

    init {
        job?.cancel()
        /*job = viewModelScope.launch {
            repo.getTransaction(reference).collect { payment ->
                _uiState.update { it.copy(payment = payment) }
            }
        }*/
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    internal companion object {
        internal fun provideFactory(repo: CardTransactionRepo): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CardDetailViewModel(repo) as T
                }
            }
    }
}

internal data class CardDetailUiState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
)