package com.sakaimobile.development.sakaiclient20.ui.fragments


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

import com.sakaimobile.development.sakaiclient20.R
import kotlinx.android.synthetic.main.fragment_site_chat.*

/**
 * A simple [Fragment] subclass.
 *
 */
class SiteChatFragment : Fragment() {

    companion object {
        const val CHAT_SITE_URL: String = "CHAT_SITE_URL"
    }

    private lateinit var sitePageUrl: String
    private var chatChannelId: String? = null
    private var csrfToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: Bundle = arguments!!
        sitePageUrl = args.getString(CHAT_SITE_URL) as String
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_site_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings: WebSettings = chatRoomWebView.settings
        settings.javaScriptEnabled = true
        chatRoomWebView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                evaluateChatVariables()
            }
        }

        chatRoomWebView.loadUrl(sitePageUrl)
    }

    // Suppress lint because in SitePageActivity we ensure not to load this
    // fragment if the build version does not meet the minimum (API 19)
    @SuppressLint("NewApi")
    private fun evaluateChatVariables() {
        chatRoomWebView.evaluateJavascript("currentChatChannelId") {
            chatChannelId = it
            tryEnableInput()
        }

        chatRoomWebView.evaluateJavascript("""
            var csrftoken = document.getElementById('topForm:csrftoken').value;
            var selectedElement = document.querySelector('#Monitor');
            document.body.innerHTML = selectedElement.innerHTML;
            csrftoken;
            """) {
            csrfToken = it
            tryEnableInput()
        }
    }

    private fun tryEnableInput() {
        if(chatChannelId != null && csrfToken != null)
            chatMessage.isEnabled = true
    }
}
