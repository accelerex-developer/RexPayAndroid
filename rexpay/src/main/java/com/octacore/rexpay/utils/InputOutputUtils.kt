@file:JvmSynthetic

package com.octacore.rexpay.utils

import android.content.Context
import java.io.File
import java.io.IOException


/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 04/02/2024
 **************************************************************************************************/
internal object InputOutputUtils {

    @Throws(IOException::class)
    fun generateTempFile(fileName: String, content: String?, context: Context): File? {
        if (content.isNullOrEmpty()) throw NullPointerException()
        val dir = File(context.cacheDir.path + "/keys")
        if (dir.exists().not())  {
            dir.mkdir()
        }
        val tempFile = File.createTempFile(fileName, ".asc", dir)
        tempFile.bufferedWriter().use { it.write(content) }
        return tempFile
    }

    @Throws(IOException::class)
    fun clearCache(context: Context) {
        val dir = File(context.cacheDir.path + "/keys")
        if (dir.exists() && dir.isDirectory) {
            val tempFiles: Array<out File>? = dir.listFiles()
            // Iterate through the files in the directory and delete them
            if (tempFiles != null) {
                for (tempFile in tempFiles) {
                    tempFile.delete()
                }
            }
        }
    }
}