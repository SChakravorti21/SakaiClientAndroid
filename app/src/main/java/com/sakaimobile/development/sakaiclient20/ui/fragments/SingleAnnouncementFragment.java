package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
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
    public static final String ANNOUNCEMENT_POSITION = "ANNOUNCEMENT_POSITION";

    private TextView closeButton;
    private int position;
    private Announcement announcement;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        announcement = (Announcement) b.getSerializable(SINGLE_ANNOUNCEMENT);
        position = b.getInt(ANNOUNCEMENT_POSITION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition moveTransition = TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move);
            Transition slideTransition = TransitionInflater.from(getContext()).inflateTransition(android.R.transition.slide_right);
            setSharedElementEnterTransition(moveTransition);
            setSharedElementReturnTransition(null);
            setReturnTransition(slideTransition);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_announcement, container, false);
        ViewCompat.setTransitionName(view, ANNOUNCEMENT_TRANSITION + position);

        this.closeButton = view.findViewById(R.id.announcement_close_button);
        TextView titleTxt = view.findViewById(R.id.announcement_title);
        TextView authorTxt = view.findViewById(R.id.author_name);
        TextView courseTxt = view.findViewById(R.id.course_name);
        TextView contentTxt = view.findViewById(R.id.announcement_content);
        TextView dateTxt = view.findViewById(R.id.date_text);
        TextView attachmentsView = view.findViewById(R.id.announcement_attachments);

        //if the title won't fit on the text box, make it scrollable
        titleTxt.setText(announcement.title);
        authorTxt.setText(announcement.createdBy);
        courseTxt.setText(announcement.courseTitle);
        dateTxt.setText(announcement.getLongFormattedDate());
        contentTxt.setText(HtmlUtils.getSpannedFromHtml(announcement.body));
        contentTxt.setMovementMethod(CustomLinkMovementMethod.getInstance());
        HtmlUtils.constructAttachmentsView(attachmentsView, announcement.attachments);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.closeButton.setOnClickListener(v -> {
            // Pop this fragment off the back stack (using onBackPressed in case
            // this fragment is ever refactored to an Activity)
            if(getActivity() != null) getActivity().onBackPressed();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.closeButton.setOnClickListener(null);
        this.closeButton = null;
    }
}