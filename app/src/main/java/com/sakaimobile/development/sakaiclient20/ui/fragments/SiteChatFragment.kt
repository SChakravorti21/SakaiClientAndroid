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
import android.widget.Toast

import com.sakaimobile.development.sakaiclient20.R
import com.sakaimobile.development.sakaiclient20.networking.services.ChatService
import dagger.android.support.AndroidSupportInjection
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
    private val compositeDisposable = CompositeDisposable()

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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun postMessage() {
        if(!chatMessage.isEnabled)
            return

        val message = chatMessage.text.toString()
        chatService.postChatMessage(chatChannelId, message, csrfToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<ResponseBody> {
                    override fun onSuccess(t: ResponseBody) {
                        updateMonitor()
                        chatMessage.text.clear()
                    }
                    override fun onError(e: Throwable) {
                        Toast.makeText(context, "Failed to send message!", Toast.LENGTH_SHORT)
                                .show()
                        e.printStackTrace()
                    }
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }
                })
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
            var monitor = document.querySelector('#Monitor');
            document.body.innerHTML = monitor.innerHTML;

            $('body').css({'background': 'white'})
            $('.chatList').css({'padding': '0em'})
            $("<style type='text/css'> li { list-style: none; border: 1px grey solid; padding: 6px; margin: 8px 12px; border-radius: 6px; box-shadow:-3px 3px 3px lightgrey; }</style>").appendTo("head");

            // Returns the csrf token
            document.getElementById('topForm:csrftoken').value;
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

    private fun tryEnableInput() {
        if(chatChannelId != null && csrfToken != null) {
            updateMonitor()
            chatMessage.isEnabled = true
            progressbar.visibility = View.GONE
            chatRoomWebView.visibility = View.VISIBLE
        }
    }
}
