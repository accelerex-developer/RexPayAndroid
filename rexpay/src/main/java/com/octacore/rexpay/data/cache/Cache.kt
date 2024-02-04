@file:JvmSynthetic

package com.octacore.rexpay.data.cache

import com.octacore.rexpay.domain.models.PayPayload

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 03/02/2024
 **************************************************************************************************/


internal interface Cache {

    var payload: PayPayload?

    var ussdCode: String?

    /*fun setPayload(value: PayPayload?)

    fun setUssdCode(value: String?)

    fun getPayload(): PayPayload?

    fun getUssdCode(): String?*/

    companion object {
        @Volatile
        private var INSTANCE: Cache? = null

        @JvmStatic
        fun getInstance(): Cache {
            return INSTANCE ?: synchronized(this) {
                val instance = CacheManager()
                INSTANCE = instance
                instance
            }
        }
    }
}