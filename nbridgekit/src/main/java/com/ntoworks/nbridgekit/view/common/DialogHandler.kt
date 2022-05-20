package nbridgekit.view.common

import android.webkit.JsResult
import android.webkit.WebView

interface DialogHandler {
    fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean
    fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean
}