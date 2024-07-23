package com.ntoworks.nbridgekit.plugin.base

import android.app.Activity
import android.content.Context
import com.ntoworks.nbridgekit.view.common.BridgeScriptInterface
import org.json.JSONArray
import org.json.JSONObject

abstract class PluginBase(val scriptInterface: BridgeScriptInterface, val service: String) {
    val context: Context = scriptInterface.context
    var activity: Activity = scriptInterface.context as Activity

    companion object {
        const val INVALID_ACTION_NAME = "invalid_action_name"
        const val INVALID_PARAMETER = "invalid_parameter"
    }

    abstract fun execute(promiseId: String, command: JSONObject)

    /**
     * 정의되지 않은 파라미터가 들어왔을때 에러 전달
     * @param promiseId 전달받을 nbridge의 promiseID
     */
    protected fun invalidParamError(promiseId: String) {
        this.scriptInterface.resultListener.onPromiseResolve(
            promiseId,
            "\"$INVALID_PARAMETER\"",
            true
        )
    }

    /**
     * 정의되지 않은 액션이 들어왔을때 에러 전달
     * @param promiseId 전달받을 nbridge의 promiseID
     */
    protected fun invalidActionError(promiseId: String) {
        this.scriptInterface.resultListener.onPromiseResolve(
            promiseId,
            "\"$INVALID_ACTION_NAME\"",
            true
        )
    }


    /**
     * 입력문자를 성공으로 전달.
     * isKeepAlive 값이 true일 경우 promiseId를 제거하지 않음.
     * @param promiseId 전달받을 nbridge의 promiseID
     * @param message 전달할 메세지(default = "")
     * @param isKeepAlive promiseId 제거 여부(default = false)
     */
    protected fun sendSuccessResult(
        promiseId: String,
        message: String = "",
        isKeepAlive: Boolean = false
    ) {
        if (isKeepAlive) this.scriptInterface.resultListener.onPromiseResolve(
            promiseId,
            "\"$message\""
        )
        else this.scriptInterface.resultListener.onPromiseFinalResolve(promiseId, "\"$message\"")
    }

    /**
     * JSONObject를 성공으로 전달
     * isKeepAlive 값이 true일 경우 promiseId를 제거하지 않음.
     * @param promiseId 전달받을 nbridge의 promiseID
     * @param message 전달할 JSONObject
     * @param isKeepAlive promiseId 제거 여부(default = false)
     */
    protected fun sendSuccessResult(
        promiseId: String,
        message: JSONObject,
        isKeepAlive: Boolean = false
    ) {
        if (isKeepAlive) this.scriptInterface.resultListener.onPromiseResolve(
            promiseId,
            message.toString()
        )
        else this.scriptInterface.resultListener.onPromiseFinalResolve(
            promiseId,
            message.toString()
        )
    }

    /**
     * JSONArray를 성공으로 전달
     * isKeepAlive 값이 true일 경우 promiseId를 제거하지 않음.
     * @param promiseId 전달받을 nbridge의 promiseID
     * @param message 전달할 JSONArray
     * @param isKeepAlive promiseId 제거 여부(default = false).
     */
    protected fun sendSuccessResult(
        promiseId: String,
        message: JSONArray,
        isKeepAlive: Boolean = false
    ) {
        if (isKeepAlive) this.scriptInterface.resultListener.onPromiseResolve(
            promiseId,
            message.toString()
        )
        else this.scriptInterface.resultListener.onPromiseFinalResolve(
            promiseId,
            message.toString()
        )
    }

    /**
     * 입력문자를 에러로 전달.
     * isKeepAlive 값이 true일 경우 promiseId를 제거하지 않음.
     * @param promiseId 전달받을 nbridge의 promiseID
     * @param message 전달할 메세지(default = "")
     * @param isKeepAlive promiseId 제거 여부(default = false)
     */
    protected fun sendErrorResult(
        promiseId: String,
        message: String = "",
        isKeepAlive: Boolean = false
    ) {
        if (isKeepAlive) this.scriptInterface.resultListener.onPromiseResolve(
            promiseId,
            "\"$message\"",
            true
        )
        else this.scriptInterface.resultListener.onPromiseFinalResolve(
            promiseId,
            "\"$message\"",
            true
        )
    }
}