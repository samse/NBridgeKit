package com.samse.nbridgekit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ntoworks.nbridgekit.view.BridgeBaseActivity
import nbridgekit.view.BaseActivity

class MainActivity : BridgeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main, R.id.nbridge_root_view, R.id.nbridge_webView, R.id.nbridge_splash)
        super.onCreate(savedInstanceState)
//        webWindow.loadUrl("https://www.ntoworks.com/app/nbridge/v2/sample.html")
        webWindow.loadUrl("https://www.daum.net")
        Handler(Looper.getMainLooper()).postDelayed({hideSplash()}, 300);
    }
}