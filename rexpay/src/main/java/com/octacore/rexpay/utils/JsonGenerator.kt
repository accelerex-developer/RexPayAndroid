@file:JvmSynthetic

package com.octacore.rexpay.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.IOException

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 30/01/2024
 **************************************************************************************************/


internal inline fun <reified T> Context.listFromAsset(fileName: String): List<T> {
    val finalList = mutableListOf<T>()
    val jsonArray =
        JsonParser.parseString(readAssetsTxt(fileName, this)).asJsonArray
    for (item in jsonArray) {
        val bean = Gson().fromJson(item, T::class.java)
        finalList.add(bean)
    }
    return finalList
}

private fun readAssetsTxt(fileName: String, context: Context): String? {
    try {
        val `is` = context.assets.open(fileName)
        val size = `is`.available()
        // Read the entire asset into a local byte buffer.
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        // Convert the buffer into a string.
        return String(buffer, charset("utf-8"))
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}