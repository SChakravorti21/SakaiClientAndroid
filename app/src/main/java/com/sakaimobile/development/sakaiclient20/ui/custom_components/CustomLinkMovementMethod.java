package com.sakaimobile.development.sakaiclient20.ui.custom_components;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment;

import java.lang.ref.WeakReference;

import androidx.fragment.app.FragmentManager;

/**
 * Created by Shoumyo Chakravorti on 6/23/18.
 *
 * A {@code CustomLinkMovementMethod} that can be set as the {@link MovementMethod}
 * of {@link TextView}s, allowing them to open any kind of link.
 */

public class CustomLinkMovementMethod extends LinkMovementMethod {

    /**
     * A MovementMethod does not have access to the Context in which
     * it is being used, hence a reference to the NavActivity's fragment
     * manager must be kept to allow the MovementMethod to create the
     * WebViews when a link is clicked.
     */
    private static WeakReference<FragmentManager> fragmentManager;

    /**
     * Singleton static field that can be reused by all {@link TextView}s
     * that need to be able to open links/attachments. Since this
     * single instance is expensive to initialize, and is easily reusable,
     * implementing the singleton pattern is just natural.
     */
    private static CustomLinkMovementMethod mInstance;

    /**
     * Implementing the Singleton pattern, returns an instance
     * of {@code CustomLinkMovementMethod} that can be used as
     * the {@code MovementMethod} of a TextView or similar view.
     * @return An instance of {@code CustomLinkMovementMethod}
     */
    public static MovementMethod getInstance() {
        if (mInstance == null)
            mInstance = new CustomLinkMovementMethod();

        return mInstance;
    }

    /**
     * Sets the instance of the {@code fragmentManager} for the
     * {@code CustomLinkMovementMethod} to use.
     * @param manager The fragment manager
     */
    public static void setFragmentManager(FragmentManager manager) {
        fragmentManager = new WeakReference<>(manager);
    }

    /**
     * Largely pulled from the Android source code except for the logic
     * of opening a {@code WebView} when a link is clicked.
     * @param widget TextView in which the clicked span resides
     * @param buffer The touched buffer
     * @param event The type of touch event (eg. up, down, left, right, etc.)
     * @return True if the touch event was handled manually, otherwise whatever
     *          super(widget, buffer, event) returns.
     */
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        // Only open webview if something was clicked, instead of swiping left/right
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            // Get the location of the click
            int x = (int) event.getX();
            int y = (int) event.getY();

            // Adjust location for padding and scroll distance
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            // Get the line at the calculated area
            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            // Get the link(s) clicked based on the selection location
            ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

            if (links.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    openWebView(links[0]);
                } else { // action == MotionEvent.ACTION_DOWN
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(links[0]),
                            buffer.getSpanEnd(links[0]));
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }


    /**
     * Using the {@code fragmentManager}, creates a {@code WebFragment}
     * that opens up the given link
     * @param clickedLink The link that was clicked and should be opened
     */
    private void openWebView(ClickableSpan clickedLink) {
        // Check that the Clickable link is a URL Link
        // (A URLSpan is automatically created with 'a' tags from
        // HTML)
        if( !(clickedLink instanceof URLSpan) )
            return;

        URLSpan clicked = (URLSpan) clickedLink;
        String link = clicked.getURL();

        // If a fragment manager is available, create the WebView to view the
        // link
        if(fragmentManager != null && fragmentManager.get() != null) {
            WebFragment fragment = WebFragment.newInstance(link);

            fragmentManager.get()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit,
                            R.anim.pop_enter, R.anim.pop_exit)
                    // Add the web fragment so that the previous fragment's
                    // state remains the same after returning
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}