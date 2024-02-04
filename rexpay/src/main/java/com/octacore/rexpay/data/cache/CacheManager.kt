@file:JvmSynthetic

package com.octacore.rexpay.data.cache

import com.octacore.rexpay.domain.models.BankAccount
import com.octacore.rexpay.domain.models.PayPayload

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 03/02/2024
 **************************************************************************************************/
internal class CacheManager : Cache {

    private var _payload: PayPayload? = null
    private var _ussdCode: String? = null
    private var _bankAccount: BankAccount? = null

    override var payload: PayPayload?
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
}