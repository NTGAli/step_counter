package com.ntg.stepi.components

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TermAndConditionsScreen(){
    val mUrl = "https://stepcounter.ntgt.ir/other/term_and_conditions.html"

    AndroidView(factory = {
        WebView(it).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient()
            clearCache(true)
            loadUrl(mUrl)
        }
    }, update = {
        it.loadUrl(mUrl)
    })
}