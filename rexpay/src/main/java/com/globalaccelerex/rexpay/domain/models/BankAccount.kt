@file:JvmSynthetic

package com.globalaccelerex.rexpay.domain.models

import com.globalaccelerex.rexpay.data.remote.models.ChargeBankResponse

internal data class BankAccount(
    internal val id: Long = 0L,
    internal val bankName: String,
    internal val accountName: String,
    internal val accountNumber: String,
    internal val reference: String,
) {
    constructor(res: ChargeBankResponse?): this(
        bankName = res?.bankName ?: "",
        accountName = res?.accountName ?: "",
        accountNumber = res?.accountNumber ?: "",
        reference = res?.transactionReference ?: ""
    )
}
