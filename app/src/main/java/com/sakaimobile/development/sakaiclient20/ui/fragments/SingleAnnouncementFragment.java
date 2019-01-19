package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.sakaimobile.development.sakaiclient20.ui.helpers.HtmlUtils;


/**
 * Created by atharva on 7/12/18
 */
public class SingleAnnouncementFragment extends Fragment {

    // SHARED ELEMENT TRANSITION NAME
    public static final String ANNOUNCEMENT_TRANSITION = "ANNOUNCEMENT_TRANSITION";

    // BUNDLE ARGUMENTS
    public static final String SINGLE_ANNOUNCEMENT = "SINGLE_ANNOUNCEMENT";
    public static final String ANNOUNCEMENT_COURSE = "ANNOUNCEMENT_COURSE";
    public static final String ANNOUNCEMENT_POSITION = "ANNOUNCEMENT_POSITION";

    private int position;
    private Announcement currAnnouncement;
    private Course announcementCourse;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        currAnnouncement = (Announcement) b.getSerializable(SINGLE_ANNOUNCEMENT);
        announcementCourse = (Course) b.getSerializable(ANNOUNCEMENT_COURSE);
        position = b.getInt(ANNOUNCEMENT_POSITION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition moveTransition = TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move);
            Transition fadeTransition = TransitionInflater.from(getContext()).inflateTransition(android.R.transition.slide_right);
            setSharedElementEnterTransition(moveTransition);
            setSharedElementReturnTransition(null);
            setReturnTransition(fadeTransition);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_announcement, container, false);
        ViewCompat.setTransitionName(view, ANNOUNCEMENT_TRANSITION + position);

        TextView titleTxt = view.findViewById(R.id.announcement_title);
        TextView authorTxt = view.findViewById(R.id.author_name);
        TextView courseTxt = view.findViewById(R.id.course_name);
        TextView contentTxt = view.findViewById(R.id.announcement_content);
        TextView dateTxt = view.findViewById(R.id.date_text);
        TextView attachmentsView = view.findViewById(R.id.announcement_attachments);

        //if the title won't fit on the text box, make it scrollable
        titleTxt.setText(currAnnouncement.title);
        authorTxt.setText(currAnnouncement.createdBy);
        courseTxt.setText(announcementCourse.title);
        dateTxt.setText(currAnnouncement.getLongFormattedDate());
        contentTxt.setText(HtmlUtils.getSpannedFromHtml(currAnnouncement.body));
        contentTxt.setMovementMethod(CustomLinkMovementMethod.getInstance());
        HtmlUtils.constructAttachmentsView(attachmentsView, currAnnouncement.attachments);

        return view;
    }
}