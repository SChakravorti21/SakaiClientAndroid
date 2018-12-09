package com.example.development.sakaiclient20.ui.fragments;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;


import com.example.development.sakaiclient20.R;
import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.persistence.entities.Course;
import com.example.development.sakaiclient20.ui.MainActivity;
import com.example.development.sakaiclient20.ui.adapters.AnnouncementsAdapter;
import com.example.development.sakaiclient20.ui.listeners.LoadMoreListener;
import com.example.development.sakaiclient20.ui.listeners.OnActionPerformedListener;
import com.example.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.example.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class AnnouncementsFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    public static final int ALL_ANNOUNCEMENTS = 0;
    public static final int SITE_ANNOUNCEMENTS = 1;

    // announcements to display
    private List<Announcement> allAnnouncements;
    private HashMap<String, Course> siteIdToCourse;

    // recycler view displaying announcements
    private RecyclerView announcementRecycler;
    // adapter which puts announcements into recycler view
    private AnnouncementsAdapter adapter;

    // loads more announcements and refreshes
    private LoadMoreListener loadMoreListener;

    // announcement type (SITE or ALL)
    private int announcementType;

    // whether or not there are more announcements to load
    private boolean hasLoadedAllAnnouncements;

    private static final int ANNOUNCEMENTS_TO_GET_PER_REQUEST = 10;

    // listener for clicking on an announcement
    private OnActionPerformedListener onActionPerformedListener;


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bun = getArguments();
        announcementType =  bun.getInt(getString(R.string.announcement_type));

        // TODO check before cast
        allAnnouncements = (List<Announcement>) bun.getSerializable(getString(R.string.all_announcements_tag));
        siteIdToCourse = (HashMap<String, Course>) bun.getSerializable(getString(R.string.siteid_to_course_map));

        if (announcementType == ALL_ANNOUNCEMENTS) {
            loadMoreListener = new LoadsAllAnnouncements();
        } else if (announcementType == SITE_ANNOUNCEMENTS) {
            loadMoreListener = new LoadsSiteAnnouncements();
        }

        hasLoadedAllAnnouncements = false;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        if(context instanceof Activity) {
            Activity activity = getActivity(context);

            try {
                onActionPerformedListener = (OnActionPerformedListener) activity;
            } catch(ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnActionPerformedListener");
            }
        }
    }

    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_announcements, null);
        announcementRecycler = view.findViewById(R.id.announcements_recycler);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        createAdapterAndFillRecyclerView();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadMoreListener.refresh();
//            swipeRefreshLayout.setRefreshing(false);
        });


        return view;
    }


    /**
     * Creates a new adapter and fills the recycler view with our announcements data
     * also sets the animations for the card entry
     */
    private void createAdapterAndFillRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        announcementRecycler.setLayoutManager(layoutManager);
        announcementRecycler.setItemAnimator(new DefaultItemAnimator());

        adapter = new AnnouncementsAdapter(allAnnouncements, siteIdToCourse, announcementRecycler, announcementType);
        adapter.setClickListener(onActionPerformedListener);
        adapter.setLoadMoreListener(loadMoreListener);

        announcementRecycler.setAdapter(adapter);


        //rerun animations for card entry
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_enter);
        announcementRecycler.setLayoutAnimation(controller);

        announcementRecycler.scheduleLayoutAnimation();
    }


    /**
     * Adds the newly requested announcements to the adapter and notifies that
     * they were loaded
     *
     * @param newAnnouncements announcements to add
     */
    private void addNewAnnouncementsToAdapter(List<Announcement> newAnnouncements) {

        //remove the null element we had added to signify a loading item
        allAnnouncements.remove(allAnnouncements.size() - 1);

        int initialSize = allAnnouncements.size();

        // if there are no more new announcements to display
        // mark as finished loading, remove the loading bar, then return
        if (initialSize == newAnnouncements.size()) {

            adapter.finishedLoading();
            adapter.notifyItemRemoved(allAnnouncements.size());
            Toast.makeText(getContext(), getString(R.string.no_announcements), Toast.LENGTH_SHORT).show();
            hasLoadedAllAnnouncements = true;
            return;
        }

        // add the new announcements to our list of all announcements
        for (int i = initialSize; i < newAnnouncements.size(); i++) {
            //add the new items into all announcements
            allAnnouncements.add(newAnnouncements.get(i));
        }

        // notify the adapter that we added some new items, so it can display
        adapter.notifyItemRangeChanged(initialSize, newAnnouncements.size() - initialSize);
        adapter.finishedLoading();
    }


    /**
     * private class which holds the load more method to load more all announcements
     * also holds refreshing all announcements
     */
    private class LoadsAllAnnouncements implements LoadMoreListener {

        @Override
        public void loadMore() {

            //if we have already loaded them all, don't do anything
            if (hasLoadedAllAnnouncements) {
                adapter.finishedLoading();
                return;
            }

            //add the null so we can display a loading bar while we request
            allAnnouncements.add(null);

            //tell the adapter we added an item, so that it will actually show the loading bar
            // was throwing a recycler view error, without the post
            announcementRecycler.post(() -> {
                adapter.notifyItemInserted(allAnnouncements.size() - 1);
            });

            // get the number of announcements we have to request now,
            // based on the current number showing and the number of additional ones
            // we want
            int numAnnouncementsToRequest = allAnnouncements.size() -1 + ANNOUNCEMENTS_TO_GET_PER_REQUEST;

            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshAllData(numAnnouncementsToRequest);

        }

        @Override
        public void refresh() {

            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshAllData(MainActivity.NUM_ANNOUNCEMENTS_DEFAULT);
        }


    }

    /**
     * private class which holds the load more method to load more site announcements
     */
    private class LoadsSiteAnnouncements implements LoadMoreListener {


        @Override
        public void loadMore() {

            //if we have already loaded them all, don't do anything
            if (hasLoadedAllAnnouncements) {
                adapter.finishedLoading();
                return;
            }


            //add the null show we can display a loading bar while we request
            allAnnouncements.add(null);

            //tell the adapter we added an item
            // was throwing a recycler view error, without the post
            announcementRecycler.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(allAnnouncements.size() - 1);
                }
            });


            int numAnnouncementsToRequest = allAnnouncements.size() - 1 + ANNOUNCEMENTS_TO_GET_PER_REQUEST;

            String siteId = allAnnouncements.get(0).siteId;

            ViewModelProviders.of(getActivity(),  viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshSiteData(siteId, numAnnouncementsToRequest);

            // TODO observe add new announcements to adapter
        }


        @Override
        public void refresh() {

            String siteId = allAnnouncements.get(0).siteId;

            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshSiteData(siteId, MainActivity.NUM_ANNOUNCEMENTS_DEFAULT);
        }

    }


}


