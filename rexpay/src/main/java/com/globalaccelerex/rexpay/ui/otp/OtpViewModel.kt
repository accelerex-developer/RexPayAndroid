@file:JvmSynthetic

package com.globalaccelerex.rexpay.ui.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.globalaccelerex.rexpay.data.BaseResult
import com.globalaccelerex.rexpay.data.remote.models.AuthorizeCardResponse
import com.globalaccelerex.rexpay.domain.repo.CardTransactionRepo
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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