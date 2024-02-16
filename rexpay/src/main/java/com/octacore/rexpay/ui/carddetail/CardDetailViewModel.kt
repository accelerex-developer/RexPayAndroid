@file:JvmSynthetic

package com.octacore.rexpay.ui.carddetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.remote.models.ChargeCardResponse
import com.octacore.rexpay.domain.models.CardDetail
import com.octacore.rexpay.domain.repo.BasePaymentRepo
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.utils.CreditCardFormatter
import com.octacore.rexpay.utils.ExpiryDateFormatter
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
    private val baseRepo: BasePaymentRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardDetailUiState())
    internal val uiState = _uiState.asStateFlow()

    var cardholder by mutableStateOf(CreditCardFormatter())
    var expiryDate by mutableStateOf(ExpiryDateFormatter())
    var cvv by mutableStateOf("")
    var pin by mutableStateOf("")

    var enableButton by mutableStateOf(false)

    internal fun initiateCardPayment() {
        _uiState.update { CardDetailUiState(isLoading = true) }
        viewModelScope.launch {
            val paymentRes = baseRepo.initiatePayment()
            if (paymentRes is BaseResult.Success) {
                val card = CardDetail(
                    pan = cardholder.textFieldValue.text,
                    cvv2 = cvv,
                    pin = pin,
                    expiryDate = expiryDate.textFieldValue.text
                )
                when (val chargeRes = repo.chargeCard(card, paymentRes.result)) {
                    is BaseResult.Error -> _uiState.update { CardDetailUiState(errorMsg = chargeRes) }
                    is BaseResult.Success -> _uiState.update { CardDetailUiState(response = chargeRes.result) }
                }
            } else {
                val error = paymentRes as? BaseResult.Error
                _uiState.update { CardDetailUiState(errorMsg = error) }
            }
        }
    }

    internal fun checkValues() {
        val validCard = cardholder.isInvalid
        val validDate = expiryDate.isInvalid
        val validCvv = cvv.length == 3
        val validPin = pin.length == 4
        enableButton = validCard == false && validDate == false && validCvv && validPin
    }

    internal fun reset() {
        _uiState.update { CardDetailUiState() }
    }
    internal companion object {
        internal fun provideFactory(
            repo: CardTransactionRepo,
            baseRepo: BasePaymentRepo
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CardDetailViewModel(repo, baseRepo) as T
                }
            }
    }
}

internal data class CardDetailUiState(
    val isLoading: Boolean = false,
    val errorMsg: BaseResult.Error? = null,
    val response: ChargeCardResponse? = null,
)