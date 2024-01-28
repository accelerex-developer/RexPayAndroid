@file:JvmSynthetic

package com.octacore.rexpay.utils

import android.util.Log

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 27/01/2024
 **************************************************************************************************/
internal object LogUtils {
    private var TAG = "RexPayLog"

    var showLog: Boolean = false

    @JvmStatic
    fun init(showLog: Boolean, tag: String? = null) {
        TAG = tag ?: TAG
        this.showLog = showLog
    }

    @JvmStatic
    fun d(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.d(t, msg)
        }
    }

    @JvmStatic
    fun e(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.e(t, msg)
        }
    }

    @JvmStatic
    fun e(msg: String?, e: Exception, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.e(t, msg, e)
        }
    }

    @JvmStatic
    fun i(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.i(t, msg)
        }
    }

    @JvmStatic
    fun v(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.v(t, msg)
        }
    }

    @JvmStatic
    fun w(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.w(t, msg)
        }
    }
}