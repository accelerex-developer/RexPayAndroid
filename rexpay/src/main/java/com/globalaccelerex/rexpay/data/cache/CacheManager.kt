@file:JvmSynthetic

package com.globalaccelerex.rexpay.data.cache

import com.globalaccelerex.rexpay.domain.models.BankAccount
import com.globalaccelerex.rexpay.domain.models.Charge
import com.globalaccelerex.rexpay.domain.models.PayResult

internal class CacheManager : Cache {

    private var _session: Session? = null
    private var _payload: Charge? = null
    private var _ussdCode: String? = null
    private var _bankAccount: BankAccount? = null
    private var _transactionResult: PayResult? = null

    override var hasSession: Boolean?
        get() = _session != null
        set(value) {
            _session = if (value == true) {
                Session()
            } else {
                null
            }
        }

    override var payload: Charge?
        get() = _payload
        set(value) {
            _payload = value
        }

    override var ussdCode: String?
        get() = _ussdCode
        set(value) {
            _ussdCode = value
        }

    override var bankAccount: BankAccount?
        get() = _bankAccount
        set(value) {
            _bankAccount = value
        }

    override var transactionResult: PayResult?
        get() = _transactionResult
        set(value) {
            _transactionResult = value
        }
}