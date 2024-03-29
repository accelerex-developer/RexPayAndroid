@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.models

class Charge private constructor() {
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
        amount: Long?,
        customerName: String?,
        currency: String? = null,
        callbackUrl: String? = null,
    ) : this() {

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
        amount: Long? = this._amount,
        currency: String? = this._currency,
        userId: String? = this._userId,
        callbackUrl: String? = this._callbackUrl,
        email: String? = this._email,
        customerName: String? = this._customerName
    ): Charge {
        return Charge(
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

        other as Charge

        if (_amount != other._amount) return false
        if (_currency != other._currency) return false
        if (_userId != other._userId) return false
        if (_callbackUrl != other._callbackUrl) return false
        if (_email != other._email) return false
        return _customerName == other._customerName
    }

    override fun hashCode(): Int {
        var result = _amount.hashCode()
        result = 31 * result + _currency.hashCode()
        result = 31 * result + _userId.hashCode()
        result = 31 * result + _callbackUrl.hashCode()
        result = 31 * result + _email.hashCode()
        result = 31 * result + _customerName.hashCode()
        return result
    }

    class Builder {
        private var payload = Charge()

        @Throws(NullPointerException::class, IllegalArgumentException::class)
        fun amount(value: Long?) = apply {
            if (value == null) throw NullPointerException("Amount cannot be null")
            if (value <= 0) throw IllegalArgumentException("Amount entered is not valid")
            payload = payload.copy(amount = value)
        }

        fun currency(value: String?) = apply { payload = payload.copy(currency = value) }

        @Throws(NullPointerException::class)
        fun userId(value: String?) = apply {
            if (value.isNullOrEmpty()) throw NullPointerException("UserId cannot be null or empty")
            payload = payload.copy(userId = value)
        }

        fun callbackUrl(value: String?) = apply { payload.copy(callbackUrl = value) }

        @Throws(NullPointerException::class)
        fun email(value: String?) = apply {
            if (value.isNullOrEmpty()) throw NullPointerException("Email cannot be null or empty")
            payload = payload.copy(email = value)
        }

        @Throws(NullPointerException::class)
        fun customerName(value: String?) = apply {
            if (value.isNullOrEmpty()) throw NullPointerException("Customer name cannot be null or empty")
            payload = payload.copy(customerName = value)
        }

        fun build(): Charge = payload
    }
}