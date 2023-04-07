package com.ntoworks.nbridgekit.view

import nbridgekit.view.BaseActivity

open class BridgeBaseActivity: BaseActivity() {

    var layoutResId: Int? = null
    var rootViewId: Int? = null
    var webViewId: Int? = null
    var splashViewId: Int? = null
    fun setContentView(layoutResID: Int, rootViewId: Int, webViewId: Int, splashViewId: Int) {
        super.setContentView(layoutResID)
        this.layoutResId = layoutResID
        this.rootViewId = rootViewId
        this.webViewId = webViewId
        this.splashViewId = splashViewId
    }
}