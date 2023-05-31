package com.samse.nbridgekit

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import com.ntoworks.nbridgekit.view.BridgeBaseActivity
import nbridgekit.view.BaseActivity
import nbridgekit.view.BridgeWebViewClient
import nbridgekit.view.BridgeWebWindow

class MainActivity : BridgeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main, R.id.nbridge_root_view, R.id.nbridge_webView, R.id.nbridge_splash)
        super.onCreate(savedInstanceState)
        webWindow.loadUrl("https://www.ntoworks.com/app/nbridge/v2/sample.html")
//        webWindow.loadUrl("https://dbins23.ichc.co.kr")
        Handler(Looper.getMainLooper()).postDelayed({hideSplash()}, 300);
        WebView.setWebContentsDebuggingEnabled(true)
    }

    override fun initWebView() {
        super.initWebView()
        getWebView().webViewClient = MyWebViewClient(context = this,
            fullToRefreshFlag = webWindow.fullToRefreshFlag,
            url = webWindow.url, webWindow = webWindow)
    }
}

class MyWebViewClient(context: Context, fullToRefreshFlag: Boolean, url: String?, val webWindow: BridgeWebWindow) : BridgeWebViewClient(context,
    fullToRefreshFlag, url
) {
    override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        webWindow.showLoading()
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        webWindow.hideLoading()
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
    }
}