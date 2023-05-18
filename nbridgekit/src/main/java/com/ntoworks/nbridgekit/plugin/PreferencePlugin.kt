package nbridgekit.plugin

import android.util.Log
import nbridgekit.plugin.base.PluginBase
import com.ntoworks.nbridgekit.util.PreferenceUtil
import com.ntoworks.nbridgekit.util.crypt.AndroidRsaCipherHelper
import nbridgekit.view.common.BridgeScriptInterface
import org.json.JSONException
import org.json.JSONObject
import java.util.Base64

class PreferencePlugin(scriptInterface: BridgeScriptInterface, service: String) :
    PluginBase(scriptInterface, service) {
    companion object {
        const val ACTION_GET = "get"
        const val ACTION_SET = "set"
        const val ACTION_REMOVE = "remove"
    }

//    init {
//        AndroidRsaCipherHelper.init(applicationContext = context)
//    }

    override fun execute(promiseId: String, command: JSONObject) {
        when (command.getString(BridgeScriptInterface.ACTION)) {
            ACTION_GET -> {
                get(promiseId, command)
            }
            ACTION_SET -> {
                set(promiseId, command)
            }
            ACTION_REMOVE -> {
                remove(promiseId, command)
            }
            else -> {
                invalidActionError(promiseId)
            }
        }
    }

    private fun get(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val defaultValue: String = obj.getString("defaultValue")
            val value : String? = PreferenceUtil.getInstance().getCryptedString(key, defaultValue)
            if(value != null) {
                val res = android.util.Base64.encodeToString(value.toByteArray(), android.util.Base64.DEFAULT)
                sendSuccessResult(promiseId, res)
            } else {
                throw Exception("value is null")
            }
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId, e.localizedMessage, false)
        }
    }

    private fun set(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val value: String = obj.optString("value", "")
            PreferenceUtil.getInstance().putCryptedString(key, value)
            sendSuccessResult(promiseId)
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId, e.localizedMessage, false)
        }
    }

    private fun remove(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            PreferenceUtil.getInstance().remove(key)
            sendSuccessResult(promiseId)
            Log.d("remove", "key : $key")
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId)
        }
    }
}