package nbridgekit.view

import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.webkit.*
import android.webkit.WebSettings.LayoutAlgorithm
import com.ntoworks.nbridgekit.BuildConfig
import nbridgekit.logger.Logger
import nbridgekit.plugin.PluginManager
import nbridgekit.view.common.*

class BridgeWebWindow(
    val context: Context,
    val webView: WebView,
    val pluginManager: PluginManager,
    val bridgeWebReadyListener: BridgeReadyListener
) {

    lateinit var bridgeScriptInterface: BridgeScriptInterface
    var url: String? = null
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
        showLoading()
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
                domStorageEnabled = true
                databaseEnabled = true
                useWideViewPort = false
                allowFileAccess = true
                layoutAlgorithm = LayoutAlgorithm.NORMAL
                javaScriptCanOpenWindowsAutomatically = true
                loadsImagesAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // ???????????? ?????? ?????? ??????(true??? ?????? WebChromeClient?????? ?????? ?????? ?????? ??????) ?????????????????? ?????? ???????????? ?????? ????????????
                setSupportMultipleWindows(false)
                // ?????????
                builtInZoomControls = true
                displayZoomControls = false
            }
            addJavascriptInterface(bridgeScriptInterface, NBRIDGE_KEY)
            webChromeClient = BridgeWebChromeClient(context as Activity, dialogHandler)
            webViewClient = BridgeWebViewClient()
        }
    }

    inner class BridgeWebViewClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
            Logger.debug("onPageStarted : $url")
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            Logger.debug("onPageFinished : $url")

            if(fullToRefreshFlag){
                fullToRefreshFlag = false
                context.sendBroadcast(Intent(BaseActivity.REFRESH_LAYER_BROADCAST))
            }
            hideLoading()
            if ((context as BaseActivity) != null) {
                (context as BaseActivity).hideSplash()
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            try {
                // ?????? url??? ?????? ????????????
                request?.url?.let {
                    when {
                        url.toString().contains("tel:") -> {
                            val movePhone = Intent(Intent.ACTION_DIAL, it)
                            context.startActivity(movePhone)
                        }
                        url.toString().contains("smsto:") -> {
                            val movePhone = Intent(Intent.ACTION_SENDTO, it)
                            context.startActivity(movePhone)
                        }
                        url.toString().contains("play.google.com/store/apps/") || url.toString()
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

    var fullToRefreshFlag = false
    fun refresh() {
        webView.reload()
        fullToRefreshFlag = true
    }

}