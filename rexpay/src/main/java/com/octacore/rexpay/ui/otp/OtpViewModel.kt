@file:JvmSynthetic

package com.octacore.rexpay.ui.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.octacore.rexpay.data.BaseResult
import com.octacore.rexpay.data.remote.models.AuthorizeCardResponse
import com.octacore.rexpay.domain.repo.CardTransactionRepo
import com.octacore.rexpay.utils.LogUtils
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

internal class OtpViewModel(private val repo: CardTransactionRepo) : ViewModel() {
    private val _uiState = MutableStateFlow(OTPUiState())
    internal val uiState = _uiState.asStateFlow()

    var pin by mutableStateOf("")

    fun processTransaction() {
        _uiState.update { OTPUiState(isLoading = true) }
        viewModelScope.launch {
            val response = async { repo.authorizeTransaction(pin) }.await()
            if (response is BaseResult.Success) {
                _uiState.update { OTPUiState(response = response.result) }
            } else if (response is BaseResult.Error) {
                _uiState.update { OTPUiState(errorMsg = response) }
            }
        }
    }

    internal fun reset() {
        _uiState.update { OTPUiState() }
    }

    internal companion object {
        internal fun provideFactory(repo: CardTransactionRepo): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OtpViewModel(repo) as T
                }
            }
    }
}

internal data class OTPUiState(
    internal val isLoading: Boolean = false,
    internal val errorMsg: BaseResult.Error? = null,
    internal val response: AuthorizeCardResponse? = null
)