package com.ntoworks.nbridgekit.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ntoworks.nbridgekit.R
import com.ntoworks.nbridgekit.plugin.AppPlugin
import com.ntoworks.nbridgekit.plugin.PluginManager
import com.ntoworks.nbridgekit.plugin.PreferencePlugin
import com.ntoworks.nbridgekit.util.PreferenceUtil
import com.ntoworks.nbridgekit.view.common.BackPressedHandler
import com.ntoworks.nbridgekit.view.common.DefaultBackPressedHandler
import com.ntoworks.nbridgekit.view.common.BridgeReadyListener
import kotlin.system.exitProcess

interface ActivityResultListener {
    fun onResult(result: ActivityResult)
}
open class BaseActivity : AppCompatActivity() {

    companion object {
        const val REFRESH_LAYER_BROADCAST = "refreshLayoutBroadcast"
    }
    var pluginManager: PluginManager = PluginManager()
    lateinit var webWindow: BridgeWebWindow
    var backPressedHandler: BackPressedHandler? = null
    protected var launcher: ActivityResultLauncher<Intent>? = null
    var resultListener: ActivityResultListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getMainLayoutResourceId())
        showSplash()
        initWebView()
        initRefreshLayout()

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            resultListener?.onResult(result)
        }

        PreferenceUtil.getInstance().init(this)
        backPressedHandler = DefaultBackPressedHandler(this)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun initRefreshLayout() {
        if (webWindow.fullToRefreshFlag) {
            val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.main_refresh_layout)
            if (refreshLayout != null) {
                refreshLayout.setOnRefreshListener {
                    webWindow.refresh()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            refreshLayout.isRefreshing = false
                        }
                    }, IntentFilter(REFRESH_LAYER_BROADCAST), RECEIVER_NOT_EXPORTED)
                } else {
                    registerReceiver(object : BroadcastReceiver() {
                        override fun onReceive(p0: Context?, p1: Intent?) {
                            refreshLayout.isRefreshing = false
                        }
                    }, IntentFilter(REFRESH_LAYER_BROADCAST))
                }
            }
        }
    }

    open fun initWebView() {
        webWindow = BridgeWebWindow(this,
            getWebView(),
            pluginManager,
            object: BridgeReadyListener {
                override fun onBridgeReady() {
                }
            })
        initPlugins()
    }

    open fun initPlugins() {
        pluginManager?.run {
            addPlugin("preference",
                PreferencePlugin(webWindow.bridgeScriptInterface, "preference")
            )
            addPlugin("app",
                AppPlugin(webWindow.bridgeScriptInterface, "app")
            )
        }

    }

    fun launchActivity(intent: Intent, listener: ActivityResultListener) {
        launcher?.launch(intent)
        resultListener = listener
    }

    fun getWebView(): WebView {
        return findViewById(getMainWebViewId())
    }

    open fun getMainLayoutResourceId(): Int {
        return R.layout.bridge_main
    }

    open fun getMainWebViewId(): Int {
        return R.id.main_webView
    }

    fun getRootView(): ViewGroup? {
        return findViewById(getRootViewId()) as ViewGroup
    }

    open fun getRootViewId(): Int {
        return R.id.root_view
    }

    open fun getSplashLayoutResourceId(): Int {
        return R.id.splash
    }

    open fun showSplash() {
        val splashView = findViewById<View>(getSplashLayoutResourceId())
        splashView?.apply {
            visibility = View.VISIBLE
        }
    }

    open fun hideSplash() {
        val splashView = findViewById<View>(getSplashLayoutResourceId())
        splashView?.apply {
            visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        backPressedHandler?.apply {
            if (onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }

    /**
     * 앱 종료
     */
    open fun exitApp() {
        this.setResult(RESULT_CANCELED)
        this.finishAffinity()
        exitProcess(0)
    }


}