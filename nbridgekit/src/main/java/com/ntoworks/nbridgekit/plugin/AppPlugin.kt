package com.ntoworks.nbridgekit.plugin

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper.getMainLooper
import android.provider.Settings
import android.view.PixelCopy
import android.view.PixelCopy.OnPixelCopyFinishedListener
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ntoworks.nbridgekit.plugin.base.PluginBase
import com.ntoworks.nbridgekit.util.FileUtil
import com.ntoworks.nbridgekit.view.BaseActivity
import com.ntoworks.nbridgekit.view.common.BridgeScriptInterface
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.system.exitProcess


open class AppPlugin (
    scriptInterface: BridgeScriptInterface,
    service: String
) :
    PluginBase(scriptInterface, service) {
    companion object {
        const val ACTION_APP_INFO       = "appInfo"
        const val ACTION_EXIT           = "exit"
        const val ACTION_GO_SETTINGS    = "goSettings"
        const val ACTION_OPEN_BROWSER   = "openBrowser"
        const val ACTION_SCREENSHOT     = "screenshot"
        const val ACTION_SHARE          = "share"

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
            // 외부 브라우저 실행
            ACTION_OPEN_BROWSER -> {
                openBrowser(promiseId, command)
            }
            ACTION_SCREENSHOT -> {
                screenShot(promiseId)
            }
            ACTION_SHARE -> {
                share(promiseId, command)
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
                    }

                    else -> {
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

    /**
     * 외부 브라우저 실행
     */
    private fun openBrowser(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            obj.let {
                val url = it.optString("url", SETTING_NORMAL)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(intent)
            }
        } catch (e: JSONException) {
            invalidParamError(promiseId)
        }
    }

    private fun screenShot(promiseId: String) {
        // 저장할 경로 설정
        val path = File("${context.getExternalFilesDir("screenshots")}")
        if (!path.exists()) {
            path.mkdirs()
        }

        val webView = (activity as BaseActivity).getWebView()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val bitmap = Bitmap.createBitmap(
                webView.getWidth(),
                webView.getHeight(),
                Bitmap.Config.ARGB_8888
            )

            PixelCopy.request(activity.window, bitmap, object: OnPixelCopyFinishedListener {
                override fun onPixelCopyFinished(copyResult: Int) {
                    if (copyResult == PixelCopy.SUCCESS) {
                        val filePath = "${path}/screenshot_${System.currentTimeMillis()}.png"
                        val file = File(filePath)
                        FileUtil.saveBitmap(context, file, bitmap);
                        var json = JSONObject();
                        json.put("path", filePath);
                        sendSuccessResult(promiseId, json)
                    } else {
                        sendErrorResult(promiseId, "캡쳐를 실패하였습니다.")
                    }
                }

            }, Handler(getMainLooper()))
        } else {
            webView.isDrawingCacheEnabled = true
            webView.buildDrawingCache()
            val bitmap: Bitmap = Bitmap.createBitmap(webView.getDrawingCache())
            webView.setDrawingCacheEnabled(false)

            val filePath = "${path}/screenshot_${System.currentTimeMillis()}.png"
            val file = File(filePath)
            FileUtil.saveBitmap(context, file, bitmap);
            var json = JSONObject();
            json.put("path", filePath);
            sendSuccessResult(promiseId, json)
        }
    }

    private fun share(promiseId: String, command: JSONObject) {
        try {
            val obj: JSONObject = command.getJSONObject("option")
            if (obj.has("text")) {
                val text = obj.getString("text")
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.setType("text/plain")
                shareIntent.putExtra(Intent.EXTRA_TEXT, text)
                activity.startActivity(Intent.createChooser(shareIntent, "공유"))
            }
            if (obj.has("filePath")) {
                val filePath = obj.getString("filePath")
                val file = File(filePath)
                if (file.exists()) {
                    val uri = FileProvider.getUriForFile(context, activity.packageName + ".provider", file)

                    // 파일을 공유할 인텐트 생성
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.setType("application/pdf") // 파일 유형 설정 (PDF 파일을 가정)
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // URI 권한 부여

                    // 파일 공유 인텐트 실행
                    activity.startActivity(Intent.createChooser(shareIntent, "Share file via"))

                } else {
                    sendErrorResult(promiseId, "파일이 존재하지 않아 공유할 수 없습니다.")
                }
            }

        } catch (e: JSONException) {
            sendErrorResult(promiseId, e.localizedMessage);
        }
    }
}