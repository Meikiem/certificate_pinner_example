package com.example.sslpin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.sslpin.config.Config


class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var pinningToggle: SwitchCompat
    private lateinit var btn: Button
    private lateinit var logTextView: TextView
    private val url = "https://self-signed.badssl.com"
    private var pinningEnabled = true
    private val onLoadedListener = object : SecureWebViewClient.OnResultListener {
        override fun onLoaded(url: String) {
            runOnUiThread { logTextView.text = getString(R.string.loaded, url) }
        }

        override fun onPreventedLoading(reason: String) {
            runOnUiThread { logTextView.text = getString(R.string.loading_prevented, reason) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById<View>(R.id.webView) as WebView
        pinningToggle = findViewById(R.id.pinning_switch)
        btn = findViewById<View>(R.id.btn) as Button
        logTextView = findViewById<View>(R.id.textView) as TextView
        setupWebView(SecureWebViewClient(Config.createSecureOkHttpClient()))
        wireButton()
        wireToggle()
    }

    private fun setupWebView(webViewClient: SecureWebViewClient) {
        webViewClient.setOnLoaderListener(onLoadedListener)
        webView.webViewClient = webViewClient
    }

    private fun wireButton() {
        btn.setOnClickListener {
            webView.loadUrl("about:blank")
            logTextView.text = ""
            webView.loadUrl(url)
        }
    }

    private fun wireToggle() {
        pinningToggle.setOnCheckedChangeListener { _, isChecked ->
            pinningEnabled = isChecked
            onSwitchToggled()
        }
    }

    private fun onSwitchToggled() {
        if (pinningEnabled) {
            setupWebView(SecureWebViewClient(Config.createPinningOnlyOkHttpClient()))
            Log.d(javaClass.simpleName, "using setup for pinning only")
        } else {
            setupWebView(SecureWebViewClient(Config.insecureOkHttpClient()))
            Log.d(javaClass.simpleName, "using setup with no pinning and whitelisting")
        }
    }
}
