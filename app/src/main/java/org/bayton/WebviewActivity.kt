package org.bayton

import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webview)
        val webSettings: WebSettings = webView!!.settings
        webSettings.javaScriptEnabled = true

        // Retrieve the URL from the intent
        val urlString = intent.getStringExtra("url")

        if (urlString != null) {
            val parsedUri = Uri.parse(urlString)
            webView!!.loadUrl(parsedUri.toString())
        }

        // Ensure the WebView stays within the app
        webView!!.webViewClient = WebViewClient()
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }
}