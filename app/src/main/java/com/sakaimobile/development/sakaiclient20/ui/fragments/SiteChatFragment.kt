package com.sakaimobile.development.sakaiclient20.ui.fragments


import android.annotation.SuppressLint
import android.content.Context
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
import com.sakaimobile.development.sakaiclient20.networking.services.ChatService
import dagger.android.support.AndroidSupportInjection
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_site_chat.*
import okhttp3.ResponseBody
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class SiteChatFragment : Fragment() {

    companion object {
        const val CHAT_SITE_URL: String = "CHAT_SITE_URL"
    }

    @Inject lateinit var chatService: ChatService
    private lateinit var sitePageUrl: String
    private var chatChannelId: String? = null
    private var csrfToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: Bundle = arguments!!
        sitePageUrl = args.getString(CHAT_SITE_URL) as String
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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
        sendMessageButton.setOnClickListener {
            postMessage()
        }
    }

    // Suppress lint because in SitePageActivity we ensure not to load this
    // fragment if the build version does not meet the minimum (API 19)
    @SuppressLint("NewApi")
    private fun evaluateChatVariables() {
        chatRoomWebView.evaluateJavascript("currentChatChannelId") {
            chatChannelId = it.substring(1 until it.length - 1)
            tryEnableInput()
        }

        chatRoomWebView.evaluateJavascript("""
            var csrftoken = document.getElementById('topForm:csrftoken').value;
            var selectedElement = document.querySelector('#Monitor');
            document.body.innerHTML = selectedElement.innerHTML;
            csrftoken;
            """) {
            csrfToken = it.substring(1 until it.length - 1)
            tryEnableInput()
        }
    }

    @SuppressLint("NewApi")
    private fun updateMonitor() {
        chatRoomWebView.evaluateJavascript("""
            updateNow();
            $('html, body').animate({scrollTop:document.body.offsetHeight}, 400);
            """, null)
    }

    private fun postMessage() {
        if(!chatMessage.isEnabled)
            return

        val message = chatMessage.text.toString()
        chatMessage.text.clear()
        chatService.postChatMessage(chatChannelId, message, csrfToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<ResponseBody> {
                    override fun onSuccess(t: ResponseBody) {
                        updateMonitor()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun tryEnableInput() {
        if(chatChannelId != null && csrfToken != null) {
            chatMessage.isEnabled = true
            updateMonitor()
        }
    }
}
