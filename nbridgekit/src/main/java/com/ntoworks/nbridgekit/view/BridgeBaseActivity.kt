package com.ntoworks.nbridgekit.view

import nbridgekit.view.BaseActivity

open class BridgeBaseActivity: BaseActivity() {

    var layoutResId: Int? = null
    var rootViewId: Int? = null
    var webViewId: Int? = null
    var splashViewId: Int? = null
    fun setContentView(layoutResID: Int, rootViewId: Int, webViewId: Int, splashViewId: Int) {
        this.layoutResId = layoutResID
        this.rootViewId = rootViewId
        this.webViewId = webViewId
        this.splashViewId = splashViewId
    }

    override fun getMainLayoutResourceId(): Int {
        return if (layoutResId!=null) layoutResId!! else super.getMainLayoutResourceId()
    }

    override fun getRootViewId(): Int {
        return if (rootViewId!=null) rootViewId!! else  super.getRootViewId()
    }

    override fun getMainWebViewId(): Int {
        return if (webViewId!=null) webViewId!! else super.getMainWebViewId()
    }

    override fun getSplashLayoutResourceId(): Int {
        return if (splashViewId!=null) splashViewId!! else super.getSplashLayoutResourceId()
    }

}