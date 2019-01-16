package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;

import java.util.Map;


/**
 * Created by atharva on 7/12/18
 */
public class SingleAnnouncementFragment extends Fragment {

    private Announcement currAnnouncement;
    private Map<String, Course> siteIdToCourse;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        currAnnouncement = (Announcement) b.getSerializable(getString(R.string.single_announcement_tag));
        siteIdToCourse = (Map<String, Course>) b.getSerializable(getString(R.string.siteid_to_course_map));
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_full_announcement, null);

        TextView titleTxt = view.findViewById(R.id.announcement_title);
        TextView authorTxt = view.findViewById(R.id.author_name);
        TextView courseTxt = view.findViewById(R.id.course_name);
        TextView contentTxt = view.findViewById(R.id.announcement_content);
        TextView dateTxt = view.findViewById(R.id.date_text);

        //if the title won't fit on the text box, make it scrollable
        titleTxt.setText(currAnnouncement.title);
        titleTxt.setHorizontallyScrolling(true);
        titleTxt.setMovementMethod(new ScrollingMovementMethod());

        authorTxt.setText(currAnnouncement.createdBy);
        authorTxt.setHorizontallyScrolling(true);
        authorTxt.setMovementMethod(new ScrollingMovementMethod());

        courseTxt.setText(siteIdToCourse.get(currAnnouncement.siteId).title);
        courseTxt.setHorizontallyScrolling(true);
        courseTxt.setMovementMethod(new ScrollingMovementMethod());

        contentTxt.setMovementMethod(new ScrollingMovementMethod());

        //if its after android N, use this method for setting the html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentTxt.setText(Html.fromHtml(currAnnouncement.body, Html.FROM_HTML_MODE_COMPACT));
        } else {
            contentTxt.setText(Html.fromHtml(currAnnouncement.body));
        }

        dateTxt.setText(currAnnouncement.getLongFormattedDate());

        return view;

    }
}