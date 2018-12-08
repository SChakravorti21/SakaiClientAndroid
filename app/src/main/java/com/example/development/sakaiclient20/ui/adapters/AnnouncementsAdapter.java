package com.example.development.sakaiclient20.ui.adapters;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.example.development.sakaiclient20.ui.helpers.RutgersSubjectCodes;
import com.example.development.sakaiclient20.ui.listeners.AnnouncementItemClickListener;
import com.example.development.sakaiclient20.ui.listeners.LoadMoreListener;

import java.util.ArrayList;

/**
 * Created by atharva on 7/8/18
 */
public class AnnouncementsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private int announcementType;

    // type of item to display (item = announcement, load = loading bar)
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_LOAD = 1;

    // how many announcements should be between the last visible one
    // and the last one before we request more
    private static final int END_OFFSET_BEFORE_RELOAD = 5;

    // list of announcements to display
    private ArrayList<Announcement> announcements;
    // click listener for each announcement card
    private AnnouncementItemClickListener clickListener;

    // number of total announcements displaying
    private int numItems;
    // the index of the last item thats completely visible on the screen
    private int lastVisibleItemPos;
    // whether or not we are currently loading more announcements
    private boolean isLoading;
    // listener interface that contains the loadMore function
    private LoadMoreListener loadMoreListener;


    public AnnouncementsAdapter(ArrayList<Announcement> announcementCollections,
                                RecyclerView announcementsRecycler,
                                int type) {

        announcements = announcementCollections;
        announcementType = type;


        final LinearLayoutManager manager = (LinearLayoutManager)announcementsRecycler.getLayoutManager();
        announcementsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                numItems = manager.getItemCount();
                lastVisibleItemPos = manager.findLastCompletelyVisibleItemPosition();

                // load more if:
                //      we aren't already loading
                //      if the last visible item position is within our end offset
                //      if the last visible item isn't the last item in our list
                //          this one is needed b/c otherwise, if there are only two
                //          announcements in the list, it will try to request more
                if(!isLoading && lastVisibleItemPos >= numItems - END_OFFSET_BEFORE_RELOAD
                        && lastVisibleItemPos < numItems - 1) {
                    isLoading = true;
                    loadMoreListener.loadMore();
                }
            }
        });

    }

    /**
     * View holder for each announcement card
     */
    public class AnnouncementItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public TextView courseIcon;
        public TextView authorTxt;
        public TextView date;
        public TextView cardHeading2;
        public TextView cardHeading3;


        public AnnouncementItemViewHolder(View cardView) {
            //give this card view to the reycler view's view holder
            super(cardView);

            //save all of the views we will need to change
            authorTxt = cardView.findViewById(R.id.author_name);
            courseIcon = cardView.findViewById(R.id.course_icon);
            cardHeading3 = cardView.findViewById(R.id.title_txt);
            date = cardView.findViewById(R.id.date_text);
            cardHeading2 = cardView.findViewById(R.id.course_name);

            cardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onClickAnnouncement(v, getAdapterPosition());
        }
    }


    /**
     * View holder for the loading spinner
     */
    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.loadMoreProgressBar);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_announcements, parent, false);
            return new AnnouncementItemViewHolder(itemView);
        } else if (viewType == TYPE_LOAD) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false);
            return new LoadingViewHolder(itemView);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // create announcement item
        if (holder instanceof AnnouncementItemViewHolder) {
            //set the data inside the card

            Announcement currAnnouncement = announcements.get(position);

            AnnouncementItemViewHolder announcementHolder = (AnnouncementItemViewHolder) holder;

            announcementHolder.authorTxt.setText(currAnnouncement.getCreatedByDisplayName());

            int subjCode = DataHandler.getSubjectCodeFromId(currAnnouncement.getSiteId());
            announcementHolder.courseIcon.setText(RutgersSubjectCodes.mapCourseCodeToIcon.get(subjCode));

            announcementHolder.date.setText(currAnnouncement.getShortFormattedDate());


            //check to see the announcement type
            if(announcementType == AnnouncementsFragment.ALL_ANNOUNCEMENTS) {

                // if all announcements, show course title, then announcement title
                announcementHolder.cardHeading2.setText(DataHandler.getTitleFromId(currAnnouncement.siteId));
                announcementHolder.cardHeading3.setText(currAnnouncement.title);

            }
            else if(announcementType == AnnouncementsFragment.SITE_ANNOUNCEMENTS) {

                //if site announcements, show announcement title, then announcement body
                announcementHolder.cardHeading2.setText(currAnnouncement.title);

                //if its after android N, use this method for setting the html
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    announcementHolder.cardHeading3.setText(Html.fromHtml(currAnnouncement.body, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    announcementHolder.cardHeading3.setText(Html.fromHtml(currAnnouncement.body));
                }
            }



        } else if (holder instanceof LoadingViewHolder) {
            // creating a loading item
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }


    }

    @Override
    public int getItemViewType(int position) {

        //if its null, make it view type load, otherwise item
        return announcements.get(position) == null ? TYPE_LOAD : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }


    public void setClickListener(AnnouncementItemClickListener assignmentClickListener) {
        this.clickListener = assignmentClickListener;
    }

    public void setLoadMoreListener(LoadMoreListener listener) {
        this.loadMoreListener = listener;
    }

    public void finishedLoading() {
        this.isLoading = false;
    }


}
