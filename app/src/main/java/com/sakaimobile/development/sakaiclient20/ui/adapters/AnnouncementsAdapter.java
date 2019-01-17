package com.sakaimobile.development.sakaiclient20.ui.adapters;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SingleAnnouncementFragment;
import com.sakaimobile.development.sakaiclient20.ui.helpers.CourseIconProvider;
import com.sakaimobile.development.sakaiclient20.ui.listeners.OnAnnouncementSelected;

import java.util.List;
import java.util.Map;

/**
 * Created by atharva on 7/8/18
 */
public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementItemViewHolder> {


    private int announcementType;

    // list of announcements to display
    private List<Announcement> announcements;
    // mapping siteIdToCourse, needed to get subject code and course title
    private Map<String, Course> siteIdToCourse;
    // click listener for each announcement card
    private OnAnnouncementSelected announcementclickListener;

    private LinearLayoutManager manager;

    public AnnouncementsAdapter(List<Announcement> announcements,
                                Map<String, Course> siteIdToCourse,
                                RecyclerView recyclerView,
                                int type) {

        this.announcements = announcements;
        this.announcementType = type;
        this.siteIdToCourse = siteIdToCourse;

        manager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    public int getCurScrollPos() {
        return manager.findFirstCompletelyVisibleItemPosition();
    }


    /**
     * View holder for each announcement card
     */
    class AnnouncementItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView courseIcon;
        TextView authorTxt;
        TextView date;
        TextView courseNameTxt;
        TextView announcementTitleTxt;


        AnnouncementItemViewHolder(View cardView) {
            //give this card view to the reycler view's view holder
            super(cardView);

            //save all of the views we will need to change
            authorTxt = cardView.findViewById(R.id.author_name);
            courseIcon = cardView.findViewById(R.id.course_icon);
            announcementTitleTxt = cardView.findViewById(R.id.title_txt);
            date = cardView.findViewById(R.id.date_text);
            courseNameTxt = cardView.findViewById(R.id.course_name);

            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Announcement announcementToExpand = announcements.get(pos);
            Course course = siteIdToCourse.get(announcementToExpand.siteId);
            announcementclickListener.onAnnouncementSelected(announcementToExpand, course, v, pos);
        }
    }


    @Override
    public AnnouncementItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_announcements, parent, false);
        return new AnnouncementItemViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(AnnouncementItemViewHolder holder, int position) {
        //set the data inside the card
        Announcement currAnnouncement = announcements.get(position);

        int subjCode = siteIdToCourse.get(currAnnouncement.siteId).subjectCode;
        holder.courseIcon.setText(CourseIconProvider.getCourseIcon(subjCode));

        holder.authorTxt.setText(currAnnouncement.createdBy);
        holder.date.setText(currAnnouncement.getShortFormattedDate());
        holder.courseNameTxt.setText(siteIdToCourse.get(currAnnouncement.siteId).title);
        holder.announcementTitleTxt.setText(currAnnouncement.title);

        ViewCompat.setTransitionName(holder.itemView, SingleAnnouncementFragment.ANNOUNCEMENT_TRANSITION + position);
    }


    @Override
    public int getItemCount() {
        return announcements == null ? 0 : announcements.size();
    }


    public void setClickListener(OnAnnouncementSelected announcementclickListener) {
        this.announcementclickListener = announcementclickListener;
    }

}
