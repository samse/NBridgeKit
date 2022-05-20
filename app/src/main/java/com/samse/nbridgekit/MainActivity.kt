package com.samse.nbridgekit

import android.os.Bundle
import nbridgekit.view.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        webWindow.loadUrl("https://www.ntoworks.com/app/nbridge/v2/sample.html")
        webWindow.loadUrl("https://www.daum.net")
    }
}