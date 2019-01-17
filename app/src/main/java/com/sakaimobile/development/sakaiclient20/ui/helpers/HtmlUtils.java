package com.sakaimobile.development.sakaiclient20.ui.helpers;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;

import java.util.List;

public class HtmlUtils {

    /**
     * Takes an HTML {@link String} and converts it into a {@link Spanned}
     * object based on the user's build version.
     * @param text The text to convert
     * @return The {@link Spanned} object that can be used to set the content of a {@link TextView}.
     */
    public static Spanned getSpannedFromHtml(String text) {
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(text);
        }
        return description;
    }

    /**
     * Constructs the attachments {@link TextView} to display the downloadable
     * {@link Attachment} objects for an {@link Assignment} or {@link Announcement}. A {@link TextView}
     * can accept a {@link Spanned} object representing some HTML, so {@code a}
     * tags are used to make the attachments clickable, which in turn opens
     * a {@link com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment}
     * (initiated by the {@link android.text.method.MovementMethod}
     * {@link CustomLinkMovementMethod}).
     */
    public static void constructAttachmentsView(TextView attachmentsView, List<Attachment> attachments) {
        // If there are no attachments, do not show the attachments view.
        // Otherwise, construct the HTML and set the TextView's content from that.
        if(attachments == null || attachments.size() == 0) {
            attachmentsView.setText(HtmlUtils.getSpannedFromHtml("<p>No attachments found.</p>"));
        } else {
            StringBuilder attachmentsString = new StringBuilder();
            for (Attachment attachment : attachments) {
                attachmentsString.append("<p><a href=\"")
                        .append(attachment.url)
                        .append("\">")
                        .append(attachment.name)
                        .append("</a></p>");
            }

            Spanned attachmentBody = HtmlUtils.getSpannedFromHtml(attachmentsString.toString());
            attachmentsView.setText(attachmentBody);

            // The MovementMethod handles creation of a WebFragment
            // whenever a URLSpan is clicked (the WebFragment handles
            // download of attachments if it is possible).
            attachmentsView.setMovementMethod(CustomLinkMovementMethod.getInstance());
        }

    }
}
