package nbridgekit.plugin

import android.util.Log
import nbridgekit.plugin.base.PluginBase
import com.ntoworks.nbridgekit.util.PreferenceUtil
import com.ntoworks.nbridgekit.util.crypt.AndroidRsaCipherHelper
import nbridgekit.view.common.BridgeScriptInterface
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Array.setLong
import java.util.Base64

class PreferencePlugin(scriptInterface: BridgeScriptInterface, service: String) :
    PluginBase(scriptInterface, service) {
    companion object {
        const val ACTION_GET = "get"
        const val ACTION_SET = "set"
        const val ACTION_GET_BOOLEAN = "getBoolean"
        const val ACTION_SET_BOOLEAN = "setBoolean"
        const val ACTION_GET_INT = "getInt"
        const val ACTION_SET_INT = "setInt"
        const val ACTION_GET_LONG = "getLong"
        const val ACTION_SET_LONG = "setLong"
        const val ACTION_REMOVE = "remove"
    }

//    init {
//        AndroidRsaCipherHelper.init(applicationContext = context)
//    }

    override fun execute(promiseId: String, command: JSONObject) {
        when (command.getString(BridgeScriptInterface.ACTION)) {
            ACTION_GET -> get(promiseId, command)
            ACTION_SET -> set(promiseId, command)
            ACTION_GET_BOOLEAN -> getBoolean(promiseId, command)
            ACTION_SET_BOOLEAN -> setBoolean(promiseId, command)
            ACTION_GET_INT -> getInt(promiseId, command)
            ACTION_SET_INT -> setInt(promiseId, command)
            ACTION_GET_LONG -> getLong(promiseId, command)
            ACTION_SET_LONG -> setLong(promiseId, command)
            ACTION_REMOVE ->
                remove(promiseId, command)
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

    private fun getBoolean(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val defaultValue: Boolean = obj.getBoolean("defaultValue")
            val value : Boolean? = PreferenceUtil.getInstance().getBoolean(key, defaultValue)
            if(value != null) {
                sendSuccessResult(promiseId, if (value) "true" else "false")
            } else {
                throw Exception("value is null")
            }
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId, e.localizedMessage, false)
        }
    }

    private fun setBoolean(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val value: Boolean = obj.optBoolean("value", false)
            PreferenceUtil.getInstance().putBoolean(key, value)
            sendSuccessResult(promiseId)
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId, e.localizedMessage, false)
        }
    }
    private fun getInt(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val defaultValue: Int = obj.getInt("defaultValue")
            val value : Int? = PreferenceUtil.getInstance().getInt(key, defaultValue)
            if(value != null) {
                sendSuccessResult(promiseId, "$value")
            } else {
                throw Exception("value is null")
            }
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId, e.localizedMessage, false)
        }
    }

    private fun setInt(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val value: Int = obj.optInt("value", 0)
            PreferenceUtil.getInstance().putInt(key, value)
            sendSuccessResult(promiseId)
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId, e.localizedMessage, false)
        }
    }
    private fun getLong(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val defaultValue: Long = obj.getLong("defaultValue")
            val value : Long? = PreferenceUtil.getInstance().getLong(key, defaultValue)
            if(value != null) {
                sendSuccessResult(promiseId, "$value")
            } else {
                throw Exception("value is null")
            }
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        } catch (e: Exception) {
            sendErrorResult(promiseId, e.localizedMessage, false)
        }
    }

    private fun setLong(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            val key: String = obj.getString("key")
            val value: Long = obj.optLong("value", 0)
            PreferenceUtil.getInstance().putLong(key, value)
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