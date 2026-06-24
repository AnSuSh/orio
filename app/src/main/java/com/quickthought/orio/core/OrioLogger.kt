package com.quickthought.orio.core

import timber.log.Timber

object OrioLogger {

    fun infoLog(message: String, vararg args: Any?) {
        Timber.i("$message args: ${args.contentToString()}")
    }

    fun debugLog(message: String, vararg args: Any?) {
        Timber.d("$message args: ${args.contentToString()}")
    }

    fun errorLog(message: String, vararg args: Any?) {
        Timber.e("$message args: ${args.contentToString()}")
    }
}