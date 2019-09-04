package com.sakaimobile.development.sakaiclient20.ui.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment

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
        // The tag by which the chat site URL is passed in the bundle
        const val CHAT_SITE_URL: String = "CHAT_SITE_URL"
    }

    @Inject lateinit var chatService: ChatService
    private lateinit var sitePageUrl: String
    private var chatChannelId: String? = null
    private var csrfToken: String? = null
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The site page URL will be loaded in the WebView
        // and directly send us to the chat room
        val args: Bundle = arguments!!
        sitePageUrl = args.getString(CHAT_SITE_URL) as String
    }

    override fun onAttach(context: Context?) {
        // Inject the ChatService for POSTing messages
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_site_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // JS needs to be enabled for styling, updating, etc.
        val settings: WebSettings = chatRoomWebView.settings
        settings.javaScriptEnabled = true

        chatRoomWebView.webViewClient = object: WebViewClient() {
            // Keep track of whether the main URL has finished loading,
            // as users cannot click on links unless they are visible
            private var chatLoaded = false

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // Manually handle URL loading because otherwise it breaks the back stack
                // Ensure that: the main chat page has finished loading (to avoid
                // handling redirects) AND the page change is a result of the user clicking
                // a link AND the url is not null (if null there is nothing we can do)
                return if(chatLoaded
                        && view?.hitTestResult?.type == WebView.HitTestResult.SRC_ANCHOR_TYPE
                        && url != null) {
                    startWebFragment(url)
                    true
                } else {
                    super.shouldOverrideUrlLoading(view, url)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // Once the chat page loads, get the chat channel ID
                // and CSRF token (needed to make POST requests)
                super.onPageFinished(view, url)
                // It is possible that the user backed out of the site page, so don't
                // do anything if the chat WebView is null
                if(chatRoomWebView == null)
                    return

                evaluateChatVariables()
                chatLoaded = true
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
        // If the EditText is not enabled yet, we haven't finished loading
        // the chat URL
        if(!chatMessage.isEnabled)
            return

        // POST the new chat message to Sakai, don't clear the EditText
        // until we know the POST request was successful (wouldn't want user to lose
        // their message)
        val message = chatMessage.text.toString()
        chatService.postChatMessage(chatChannelId, message, csrfToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: SingleObserver<ResponseBody> {
                    override fun onSuccess(t: ResponseBody) {
                        // Make the new message visible by updating and scrolling down
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

    /**
     * If the user clicks a link inside the chat room (which redirects to a page
     * other than the chat itself), then we can open a WebFragment to handle it.
     */
    private fun startWebFragment(url: String) {
        val fragmentManager = activity?.supportFragmentManager ?: return

        // We want to show the WebFragment as if it is replacing the current
        // fragment, but do not want this fragment to re-render after returning
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                // hide and add _looks_ the same as replace
                .hide(fragmentManager.fragments[0])
                .add(R.id.fragment_container, WebFragment.newInstance(url))
                .addToBackStack(null)
                .commit()
    }

    // Suppress lint because in SitePageActivity we ensure not to load this
    // fragment if the build version does not meet the minimum (API 19)
    @SuppressLint("NewApi")
    private fun evaluateChatVariables() {
        chatRoomWebView.evaluateJavascript("currentChatChannelId") {
            // This callback returns a String of a JS String, so it will be surrounded
            // in unnecessary double quotes, trim the quotes
            chatChannelId = it.substring(1 until it.length - 1)
            tryEnableInput()
        }

        // While we're getting the CSRF token, also apply some styling to the WebView
        chatRoomWebView.evaluateJavascript("""
            // Make only the chat view visible
            var monitor = document.querySelector('#Monitor');
            document.body.innerHTML = monitor.innerHTML;

            // Make the chat view pretty
            $('body').css({'background': 'white'})
            $('.chatList').css({'padding': '0em'})
            // (yes, all of this needs to be on one line, it doesn't work otherwise)
            $("<style type='text/css'> li { list-style: none; border: 1px grey solid; padding: 6px; margin: 8px 12px; border-radius: 6px; box-shadow:-3px 3px 3px lightgrey; }</style>").appendTo("head");

            // Returns the CSRF token
            document.getElementById('topForm:csrftoken').value;
            """) {
            // Same removal of unnecessary double quotes as above
            csrfToken = it.substring(1 until it.length - 1)
            tryEnableInput()
        }
    }

    @SuppressLint("NewApi")
    private fun updateMonitor() {
        // This causes the chat monitor in the WebView to make its own GET request
        // to check for new messages, after which we scroll down if there are any new messages
        chatRoomWebView.evaluateJavascript("""
            updateNow();
            $('html, body').animate({scrollTop:document.body.offsetHeight}, 400);
            """, null)
    }

    private fun tryEnableInput() {
        // BOTH the chat channel ID and CSRF token are necessary for POST requests,
        // so do not allow the user to send a message until both values are evaluated
        if(chatChannelId != null && csrfToken != null) {
            // Scroll down to bottom of chat (the chat is focused at the top by default)
            updateMonitor()
            chatMessage.isEnabled = true

            // Hide progress bar and show WebView now that we are prepared to
            // send messages
            progressbar.visibility = View.GONE
            chatRoomWebView.visibility = View.VISIBLE
        }
    }
}
