package com.ntoworks.nbridgekit.view.common

import android.content.Context

interface LoadingHandler {
    fun showLoading(context: Context, msg: String)
    fun hideLoading()
}