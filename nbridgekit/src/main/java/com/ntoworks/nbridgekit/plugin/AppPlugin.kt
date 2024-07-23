package com.ntoworks.nbridgekit.plugin

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.ntoworks.nbridgekit.plugin.base.PluginBase
import com.ntoworks.nbridgekit.view.common.BridgeScriptInterface
import org.json.JSONException
import org.json.JSONObject
import kotlin.system.exitProcess

class AppPlugin (
    scriptInterface: BridgeScriptInterface,
    service: String
) :
    PluginBase(scriptInterface, service) {
    companion object {
        const val ACTION_APP_INFO = "appInfo"
        const val ACTION_EXIT = "exit"
        const val ACTION_GO_SETTINGS = "goSettings"

        const val SETTING_NORMAL = "normal"
        const val SETTING_PUSH = "push"
        const val SETTING_LOCATION = "location"
    }
    override fun execute(promiseId: String, command: JSONObject) {
        when (command.getString(BridgeScriptInterface.ACTION)) {
            // app 정보 전달
            ACTION_APP_INFO -> {
                getAppInfo(promiseId)
            }
            // 앱 종료
            ACTION_EXIT -> {
                exit()
            }
            // 설정 화면 이동
            ACTION_GO_SETTINGS -> {
                goSettings(promiseId, command)
            }
        }
    }

    /**
     * App Info (앱명, 앱버전)
     */
    private fun getAppInfo(promiseId: String) {
        val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val aInfo: ApplicationInfo = context.applicationInfo
        val appName = aInfo.loadLabel(context.packageManager).toString()
        val appVersion = pInfo.versionName

        val jsonObject = JSONObject()
        jsonObject.put("name", appName)
        jsonObject.put("version", appVersion);
        sendSuccessResult(promiseId, jsonObject)
    }


    /**
     * 앱 종료
     */
    private fun exit() {
        ActivityCompat.finishAffinity(activity)
        exitProcess(0)
    }

    /**
     *  일반알림, 푸시알림, 위치서비스 설정화면으로 이동
     *  type : "normal", "push", "gps"
     */
    private fun goSettings(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            obj.let {
                val type = it.optString("type", SETTING_NORMAL)
                when (type) {
                    SETTING_LOCATION -> {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        sendSuccessResult(promiseId)
                    }
                    SETTING_PUSH -> {
                        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent().also { intent ->
                                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        } else {
                            Intent().also { intent ->
                                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                                intent.putExtra("app_package", context.packageName)
                                intent.putExtra("app_uid", context.applicationInfo?.uid)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                        }

                        context.startActivity(intent)
                        sendSuccessResult(promiseId)
                    } else -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                    sendSuccessResult(promiseId)
                }
                }
            }
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        }
    }
}