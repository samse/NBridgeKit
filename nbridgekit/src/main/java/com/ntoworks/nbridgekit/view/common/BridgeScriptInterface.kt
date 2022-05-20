package nbridgekit.view.common

import android.app.Activity
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import nbridgekit.plugin.base.PluginBase
import nbridgekit.plugin.PluginManager
import org.json.JSONException
import org.json.JSONObject

open class BridgeScriptInterface(open var context: Context,
                                 open var resultListener: BridgeWebResultListener,
                                 open var pluginManager: PluginManager
) {

    interface BridgeWebResultListener {
        fun onBridgeReady()
        fun onPromiseResolve(promiseId: String?, result: String?, reject:Boolean = false)
        fun onPromiseFinalResolve(promiseId: String?, result: String?, reject:Boolean = false)
    }

    @JavascriptInterface
    open fun onBridgeReady() {
        resultListener.onBridgeReady()
    }

    @JavascriptInterface
    open fun callFromWeb(command: String) {
        val commandObj: JSONObject
        val service: String
        val promiseId: String

        // validation check
        try {
            commandObj = JSONObject(command)
            service = commandObj.getString(SERVICE)
            promiseId = commandObj.getString(PROMISEID)

            //val action: String? = commandObj.getString(ACTION)
            service.also { it ->
                val plugin: PluginBase? = pluginManager.findPlugin(it)
                plugin?.let {
                    (context as Activity).runOnUiThread {
                        it.execute(promiseId, commandObj)
                    }
                }
            }
        } catch (error: JSONException) {
            Log.e("", "========================================")
            Log.e("", "  잘못된 플러그인 호출 규약입니다. $command")
            Log.e("", "========================================")
            return
        }
    }

    companion object {
        const val SERVICE = "service"
        const val ACTION = "action"
        const val PROMISEID = "promiseId"
        const val OPTION = "option"
    }

}