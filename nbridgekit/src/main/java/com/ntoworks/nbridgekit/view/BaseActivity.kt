package nbridgekit.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ntoworks.nbridgekit.R
import nbridgekit.plugin.PluginManager
import nbridgekit.plugin.PreferencePlugin
import com.ntoworks.nbridgekit.util.PreferenceUtil
import com.ntoworks.nbridgekit.view.common.BackPressedHandler
import com.ntoworks.nbridgekit.view.common.DefaultBackPressedHandler
import nbridgekit.view.common.BridgeReadyListener
import kotlin.system.exitProcess

open class BaseActivity : AppCompatActivity() {

    companion object {
        const val REFRESH_LAYER_BROADCAST = "refreshLayoutBroadcast"
    }

    var pluginManager: PluginManager = PluginManager()
    lateinit var webWindow: BridgeWebWindow
    var defaultBackPressedHandler: BackPressedHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getMainLayoutResourceId())
        showSplash()
        initWebView()
        initRefreshLayout()

        PreferenceUtil.getInstance().init(this)
        defaultBackPressedHandler = DefaultBackPressedHandler(this)
    }

    fun initRefreshLayout() {
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.main_refresh_layout)
        if (refreshLayout!=null) {
            refreshLayout.setOnRefreshListener {
                webWindow.refresh()
            }
            registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    refreshLayout.isRefreshing = false
                }
            }, IntentFilter(REFRESH_LAYER_BROADCAST))
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
        pluginManager.addPlugin("preference",
            PreferencePlugin(webWindow.bridgeScriptInterface, "preference")
        )
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
        defaultBackPressedHandler?.apply {
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