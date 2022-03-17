package com.example.sslpin

import android.util.Log
import android.webkit.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class SecureWebViewClient(private val httpClient: OkHttpClient) : WebViewClient() {
    private var listener: OnResultListener? = null

    companion object {
        const val HEADER_CONTENT_TYPE = "Content-Type"
    }

    fun setOnLoaderListener(loaderListener: OnResultListener) {
        this.listener = loaderListener
    }

    override fun shouldInterceptRequest(
        view: WebView,
        interceptedRequest: WebResourceRequest
    ): WebResourceResponse {
        Log.d(javaClass.simpleName, interceptedRequest.url.toString())
        try {
            val url = URL(interceptedRequest.url.toString())
            val response = httpClient.newCall(
                Request.Builder()
                    .url(url)
                    .build()
            ).execute()
            val contentType = response.header(HEADER_CONTENT_TYPE)
            if (contentType != null) {
                val inputStream = response.body?.byteStream()
                val mimeType = ContentTypeParser.getMimeType(contentType)
                val charset = ContentTypeParser.getCharset(contentType)
                listener?.onLoaded(url.toString())
                return WebResourceResponse(mimeType, charset, inputStream)
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.w(javaClass.simpleName, it) }
            listener?.onPreventedLoading(ex.message ?: ex.javaClass.simpleName)
        }
        return WebResourceResponse(null, null, null)
    }

    interface OnResultListener {
        fun onLoaded(url: String)
        fun onPreventedLoading(reason: String)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        //Here we can check of the URL exists
        super.onReceivedHttpError(view, request, errorResponse)
    }
}
