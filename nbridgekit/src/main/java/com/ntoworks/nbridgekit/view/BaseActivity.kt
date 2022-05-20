package nbridgekit.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
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
        refreshLayout.setOnRefreshListener {
            webWindow.refresh()
        }
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                refreshLayout.isRefreshing = false
            }
        }, IntentFilter(REFRESH_LAYER_BROADCAST))
    }

    private fun initWebView() {
        webWindow = BridgeWebWindow(this,
            getWebView(),
            pluginManager,
            object: BridgeReadyListener {
                override fun onBridgeReady() {
                }
            })
        initPlugins()
    }

    private fun initPlugins() {
        pluginManager.addPlugin("preference",
            PreferencePlugin(webWindow.bridgeScriptInterface, "preference")
        )
    }

    fun getWebView(): WebView {
        return findViewById(getMainWebViewId())
    }

    fun getMainLayoutResourceId(): Int {
        return R.layout.bridge_main
    }

    fun getMainWebViewId(): Int {
        return R.id.main_webView
    }

    fun getSplashLayoutResourceId(): Int {
        return R.id.splash
    }


    fun showSplash() {
        val splashView = findViewById<View>(getSplashLayoutResourceId())
        splashView.visibility = View.VISIBLE
    }

    fun hideSplash() {
        val splashView = findViewById<View>(getSplashLayoutResourceId())
        splashView.visibility = View.GONE
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
    fun exitApp() {
        this.setResult(RESULT_CANCELED)
        this.finishAffinity()
        exitProcess(0)
    }
}