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

    internal var showLog: Boolean = false

    @JvmStatic
    internal fun init(showLog: Boolean, tag: String? = null) {
        TAG = tag ?: TAG
        this.showLog = showLog
    }

    @JvmStatic
    internal fun d(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.d(t, msg)
        }
    }

    @JvmStatic
    internal fun e(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.e(t, msg)
        }
    }

    @JvmStatic
    internal fun e(msg: String?, e: Exception, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.e(t, msg, e)
        }
    }

    @JvmStatic
    internal fun i(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.i(t, msg)
        }
    }

    @JvmStatic
    internal fun v(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.v(t, msg)
        }
    }

    @JvmStatic
    internal fun w(msg: String, tag: String? = null) {
        if (showLog) {
            val t = tag ?: TAG
            Log.w(t, msg)
        }
    }
}