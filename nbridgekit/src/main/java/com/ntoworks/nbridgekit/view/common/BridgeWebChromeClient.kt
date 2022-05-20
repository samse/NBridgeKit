package nbridgekit.view.common

import android.app.Activity
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView

open class BridgeWebChromeClient(val activity: Activity, val dialogHandler: DialogHandler?): WebChromeClient() {

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (dialogHandler != null) return dialogHandler.onJsAlert(view, url, message, result)
        else return super.onJsAlert(view, url, message, result)
    }

    override fun onJsConfirm(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (dialogHandler != null) return dialogHandler.onJsConfirm(view, url, message, result)
        else return super.onJsConfirm(view, url, message, result)
    }
}