@file:JvmSynthetic

package com.globalaccelerex.rexpay.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.IOException

internal inline fun <reified T> Context.listFromAsset(fileName: String): List<T> {
    val finalList = mutableListOf<T>()
    try {
        val parsedFile = readAssetsTxt(fileName, this)
        val jsonArray = JsonParser.parseString(parsedFile).asJsonArray
        for (item in jsonArray) {
            val bean = Gson().fromJson(item, T::class.java)
            finalList.add(bean)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return finalList.toList()
}

internal inline fun <reified T> Context.modelFromAssetFile(fileName: String): T? {
    val parsedFile = readAssetsTxt(fileName, this)
    val jsonItem = JsonParser.parseString(parsedFile).asJsonObject
    return try {
        Gson().fromJson(jsonItem, T::class.java)
    } catch (e: Exception) {
        null
    }
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