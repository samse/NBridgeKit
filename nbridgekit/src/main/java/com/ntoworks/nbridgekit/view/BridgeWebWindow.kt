package nbridgekit.view

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.os.Build
import android.webkit.*
import android.webkit.WebSettings.LayoutAlgorithm
import com.ntoworks.nbridgekit.BuildConfig
import com.ntoworks.nbridgekit.view.BaseActivity
import com.ntoworks.nbridgekit.view.common.BridgeWebChromeClient
import nbridgekit.logger.Logger
import nbridgekit.plugin.PluginManager
import nbridgekit.view.common.*

class BridgeWebWindow(
    val context: Context,
    val webView: WebView,
    val pluginManager: PluginManager,
    var bridgeWebReadyListener: BridgeReadyListener
) {

    lateinit var bridgeScriptInterface: BridgeScriptInterface
    public var url: String? = null
    public var fullToRefreshFlag = false
    var loadingHandler: LoadingHandler = DefaultLoadingHandler(context)
    var dialogHandler: DialogHandler = DefaultDialogHandler(context)

    companion object {
        val NBRIDGE_KEY: String = "nBridge"
    }

    init {
        initScriptInterface()
        initWebView(webView)
    }

    fun loadUrl(url: String) {
        this.url = url
        (context as Activity).runOnUiThread {
            webView?.loadUrl(url)
        }
    }

    fun showLoading() {
        (context as Activity).runOnUiThread {
            loadingHandler.showLoading(context, "")
        }
    }

    fun hideLoading() {
        (context as Activity).runOnUiThread {
            loadingHandler.hideLoading()
        }
    }

    private fun initScriptInterface() {
        this.bridgeScriptInterface = BridgeScriptInterface(
            context,
            object : BridgeScriptInterface.BridgeWebResultListener {
                override fun onBridgeReady() {
                    bridgeWebReadyListener.onBridgeReady()
                }
                override fun onPromiseResolve(promiseId: String?, result: String?, reject: Boolean) {
                    (context as Activity).runOnUiThread {
                        val formatMsg =
                            if (reject) "javascript:nbridge.resolvePromise('%s',%s, {});"
                            else "javascript:nbridge.resolvePromise('%s', %s, null);"
                        val ret = String.format(formatMsg, promiseId, result)
                        webView.loadUrl(ret)
                    }
                    if(reject) { Logger.error("promiseId: $promiseId\nresult: $result", Logger.controlStackNumber)
                    } else { Logger.sendFromNative("promiseId: $promiseId\nresult: $result", Logger.controlStackNumber) }
                }

                override fun onPromiseFinalResolve(promiseId: String?, result: String?, reject: Boolean) {
                    (context as Activity).runOnUiThread {
                        val formatMsg =
                            if (reject) "javascript:nbridge.finallyResolvePromise('%s', %s, {});"
                            else "javascript:nbridge.finallyResolvePromise('%s',%s, null);"
                        val ret = String.format(formatMsg, promiseId, result)

                        webView.loadUrl(ret)
                    }
                    if(reject) { Logger.error("promiseId: $promiseId\nresult: $result", Logger.controlStackNumber)
                    } else { Logger.sendFromNative( "promiseId: $promiseId\nresult: $result", Logger.controlStackNumber) }
                }
            }
        , pluginManager)
    }

    private fun initWebView(webView: WebView) {
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        val webWindow = this
        webView.run {
            settings.run {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    allowFileAccessFromFileURLs = true
                    allowUniversalAccessFromFileURLs = true
                }
                databaseEnabled = true
                useWideViewPort = false
                layoutAlgorithm = LayoutAlgorithm.NORMAL
                javaScriptCanOpenWindowsAutomatically = true
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // 브라우저 팝업 허용 여부(true일 경우 WebChromeClient에서 팝업 화면 구현 필요) 플러그인으로 따로 제공해줄 경우 비활성화
                setSupportMultipleWindows(false)
                // 줌기능
                builtInZoomControls = true
                displayZoomControls = false
            }
            addJavascriptInterface(bridgeScriptInterface, NBRIDGE_KEY)
            webChromeClient = BridgeWebChromeClient(context as Activity, dialogHandler)
            webViewClient = BridgeWebViewClient(context, fullToRefreshFlag, url)
        }
    }



    fun refresh() {
        webView.reload()
        fullToRefreshFlag = true
    }

}

open class BridgeWebViewClient(val context: Context, var fullToRefreshFlag: Boolean, val url: String?) : WebViewClient() {

    override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
        Logger.debug("onPageStarted : $url")
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)
        Logger.debug("onPageFinished : $url")

        if(fullToRefreshFlag) {
            fullToRefreshFlag = false
            context.sendBroadcast(Intent(BaseActivity.REFRESH_LAYER_BROADCAST))
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        try {
            // 특정 url의 경우 화면이동
            request?.url?.let {
                when {
                    it.toString().contains("tel:") -> {
                        val movePhone = Intent(Intent.ACTION_DIAL, it)
                        context.startActivity(movePhone)
                    }
                    it.toString().contains("smsto:") -> {
                        val movePhone = Intent(Intent.ACTION_SENDTO, it)
                        context.startActivity(movePhone)
                    }
                    it.toString().contains("play.google.com/store/apps/") || url.toString()
                        .contains("market://") || url.toString().contains("intent://") -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = it
                        context.startActivity(intent)
                    }
                    else -> {
                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }
            }
            return true

        } catch (e: Exception) {
            Logger.error(e.message)
            return false
        }
    }
}