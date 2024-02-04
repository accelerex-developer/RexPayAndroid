@file:JvmSynthetic

package com.octacore.rexpay.domain.models

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/

class PayPayload private constructor() {
    private var _reference: String = ""
    private var _amount: Long = 0L
    private var _currency: String = "NGN"
    private var _userId: String = ""
    private var _callbackUrl: String = ""
    private var _email: String = ""
    private var _customerName: String = ""

    @Throws(NullPointerException::class, IllegalArgumentException::class)
    constructor(
        userId: String?,
        email: String?,
        reference: String?,
        amount: Long?,
        customerName: String?,
        currency: String? = null,
        callbackUrl: String? = null,
    ) : this() {
        if (reference.isNullOrEmpty()) throw NullPointerException("Reference cannot be null or empty")
        _reference = reference

        if (amount == null) throw NullPointerException("Amount cannot be null")
        if (amount <= 0) throw IllegalArgumentException("Amount entered is not valid")
        _amount = amount

        if (userId.isNullOrEmpty()) throw NullPointerException("UserId cannot be null or empty")
        _userId = userId

        if (email.isNullOrEmpty()) throw NullPointerException("Email cannot be null or empty")
        _email = email

        if (customerName.isNullOrEmpty()) throw NullPointerException("Customer name cannot be null or empty")
        _customerName = customerName

        _currency = currency ?: _currency
        _callbackUrl = callbackUrl ?: _callbackUrl
    }

    val reference: String
        get() = _reference

    val amount: Long
        get() = _amount

    val currency: String
        get() = _currency

    val userId: String
        get() = _userId

    val callbackUrl: String
        get() = _callbackUrl

    val email: String
        get() = _email

    val customerName: String
        get() = _customerName

    internal val userInfo: String
        get() {
            return when {
                _userId.isEmpty().not() -> _userId
                _email.isEmpty().not() -> _email
                _customerName.isEmpty().not() -> _customerName
                else -> ""
            }
        }

    fun copy(
        reference: String? = this._reference,
        amount: Long? = this._amount,
        currency: String? = this._currency,
        userId: String? = this._userId,
        callbackUrl: String? = this._callbackUrl,
        email: String? = this._email,
        customerName: String? = this._customerName
    ): PayPayload {
        return PayPayload(
            reference = reference,
            amount = amount,
            currency = currency,
            userId = userId,
            callbackUrl = callbackUrl,
            email = email,
            customerName = customerName
        )
    }

    override fun toString(): String {
        return "PayPayload(" +
                "reference='$_reference', " +
                "amount=$_amount, " +
                "currency='$_currency', " +
                "userId='$_userId', " +
                "callbackUrl='$_callbackUrl', " +
                "email='$_email', " +
                "customerName='$_customerName'" +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PayPayload

        if (_reference != other._reference) return false
        if (_amount != other._amount) return false
        if (_currency != other._currency) return false
        if (_userId != other._userId) return false
        if (_callbackUrl != other._callbackUrl) return false
        if (_email != other._email) return false
        return _customerName == other._customerName
    }

    override fun hashCode(): Int {
        var result = _reference.hashCode()
        result = 31 * result + _amount.hashCode()
        result = 31 * result + _currency.hashCode()
        result = 31 * result + _userId.hashCode()
        result = 31 * result + _callbackUrl.hashCode()
        result = 31 * result + _email.hashCode()
        result = 31 * result + _customerName.hashCode()
        return result
    }

    class Builder {
        private val payload = PayPayload()

        @Throws(NullPointerException::class)
        fun reference(value: String?) = apply {
            if (value.isNullOrEmpty()) throw NullPointerException("Reference cannot be null or empty")
            payload.copy(reference = value)
        }

        @Throws(NullPointerException::class, IllegalArgumentException::class)
        fun amount(value: Long?) = apply {
            if (value == null) throw NullPointerException("Amount cannot be null")
            if (value <= 0) throw IllegalArgumentException("Amount entered is not valid")
            payload.copy(amount = value)
        }

        fun currency(value: String?) = apply { payload.copy(currency = value) }

        @Throws(NullPointerException::class)
        fun userId(value: String?) = apply {
            if (value.isNullOrEmpty()) throw NullPointerException("UserId cannot be null or empty")
            payload.copy(userId = value)
        }

        fun callbackUrl(value: String?) = apply { payload.copy(callbackUrl = value) }

        @Throws(NullPointerException::class)
        fun email(value: String?) = apply {
            if (value.isNullOrEmpty()) throw NullPointerException("Email cannot be null or empty")
            payload.copy(email = value)
        }

        @Throws(NullPointerException::class)
        fun customerName(value: String?) = apply {
            if (value.isNullOrEmpty()) throw NullPointerException("Customer name cannot be null or empty")
            payload.copy(customerName = value)
        }

        fun build(): PayPayload = payload
    }
}