package com.ntoworks.nbridgekit.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.ntoworks.nbridgekit.R
import nbridgekit.view.common.DefaultDialogHandler
import nbridgekit.view.common.DefaultLoadingHandler
import nbridgekit.view.common.DialogHandler
import nbridgekit.view.common.LoadingHandler

class PopupWebWindow(
    context: Context,
    val url: String,
    private val title:String,
    private val navigationVisibility:Boolean
): AppCompatDialog(context, R.style.FullScreenDialog) {

    var loadingHandler: LoadingHandler = DefaultLoadingHandler(context)
    var dialogHandler: DialogHandler = DefaultDialogHandler(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_webview)

        initView()
    }

    /**
     * 화면 초기화
     */
    open fun initView() {
        val webView = findViewById<WebView>(R.id.popup_webview)
        webView?.let {
            initPopupWebView(it)
        }

        // 네비게이션 visibility 초기화
        if(!navigationVisibility) {
            findViewById<ConstraintLayout>(R.id.popup_webview_top_navigation)?.visibility = View.GONE
        }

        // 타이틀이 있을 경우 타이틀 보임
        if(title.isNotEmpty()) {
            findViewById<TextView>(R.id.popup_webview_title)?.apply {
                text = title
                visibility = View.VISIBLE
            }
        }

        // 네비게이션 바에 종료 버튼 클릭 이벤트 초기화
        findViewById<ImageButton>(R.id.popup_webview_close_button)?.setOnClickListener {
            webView?.destroy()
            dismiss()
        }
        // 다이얼로그 해제시 호출
        setOnDismissListener {
            webView?.destroy()
        }
        // 다이얼로그 취소시 호출
        setOnCancelListener {
            dismiss()
        }
        // 풀화면이라서 상관없을 듯하나 넣어줌
        setCanceledOnTouchOutside(true)

        // 뒤로가기 버튼 클릭 시 페이지가 있을 경우 페이지 뒤로가기, 없을 경우 팝업 닫기
        setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView?.canGoBack() == true) {
                        webView.goBack()
                        return@setOnKeyListener true
                    } else {
                        return@setOnKeyListener false
                    }
                }
                else -> return@setOnKeyListener false
            }
        }

        // 마지막으로 팝업 웹뷰에 url 로드
        webView?.loadUrl(url)
    }

    /**
     * 팝업 웹뷰 초기화
     */
    open fun initPopupWebView(popupWebView: WebView) {
        // 팝업 웹뷰 설정
        popupWebView.apply {
            popupWebView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                useWideViewPort = false
                allowFileAccess = true
                layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                // 다운로드 활성화 시 추가 작업 필요
//                setDownloadListener(downloadListener)
//                textZoom = 100
            }

            webViewClient = customWebViewClient
            // javaScript 활성화시 추가 작업 필요

//            addJavascriptInterface(bridgeWebViewInterface, ConstantsBase.BRIDGE_WEBWINDOW_JAVASCRIPT_INTERFACE_KEY)
        }
    }

    /**
     * 커스텀 웹뷰 클라이언트(웹뷰 시작, 종료, 특정 url 로드 중 등에서 이벤트 구현)
     */
    val customWebViewClient : WebViewClient by lazy {
        object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
//                loadingHandler.showLoading(context, "")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingHandler.hideLoading()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                try{
                    // 특정 url의 경우 화면이동
                    val url = request?.url
                    url?.let{
                        when {
                            url.toString().contains("tel:") -> {
                                val movePhone = Intent(Intent.ACTION_DIAL, url)
                                context.startActivity(movePhone)
                            }
                            url.toString().contains("smsto:") -> {
                                val movePhone = Intent(Intent.ACTION_SENDTO, url)
                                context.startActivity(movePhone)
                            }
                            url.toString().contains("play.google.com/store/apps/") || url.toString().contains("market://") || url.toString().contains("intent://" ) -> {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = url
                                context.startActivity(intent)
                                dismiss()
                            }
                            else -> {
                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }
                    }
                    return true

                } catch (activityE: ActivityNotFoundException) {
                    dismiss()
                    return true
                } catch (e: Exception) {
                    dismiss()
                    return true
                }
            }
        }
    }
}