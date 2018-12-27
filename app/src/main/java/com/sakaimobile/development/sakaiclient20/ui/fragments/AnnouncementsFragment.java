package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
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

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.adapters.AnnouncementsAdapter;
import com.sakaimobile.development.sakaiclient20.ui.listeners.LoadMoreListener;
import com.sakaimobile.development.sakaiclient20.ui.listeners.OnAnnouncementSelected;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class AnnouncementsFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    public static final int NUM_ANNOUNCEMENTS_DEFAULT = 10;

    public static final int ALL_ANNOUNCEMENTS = 0;
    public static final int SITE_ANNOUNCEMENTS = 1;

    // announcements to display
    private List<Announcement> allAnnouncements;

    private HashMap<String, Course> siteIdToCourseMap;

    // recycler view displaying announcements
    private RecyclerView announcementRecycler;
    // adapter which puts announcements into recycler view
    private AnnouncementsAdapter adapter;

    // loads more announcements and refreshes
    private LoadMoreListener loadMoreListener;

    // announcement type (SITE or ALL)
    private int announcementType;

    // whether or not there are more announcements to load
    private boolean hasLoadedAllAnnouncements = false;

    private static final int ANNOUNCEMENTS_TO_GET_PER_REQUEST = 10;

    // listener for clicking on an announcement
    private OnAnnouncementSelected onAnnouncementSelected;

    private SwipeRefreshLayout swipeRefreshLayout;

    LiveData<List<Announcement>> liveData;

    // TODO remove
    String siteIdIfSiteAnnouncements;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bun = getArguments();
        siteIdIfSiteAnnouncements = bun.getString(getString(R.string.siteid_tag));
        announcementType = siteIdIfSiteAnnouncements == null ? ALL_ANNOUNCEMENTS : SITE_ANNOUNCEMENTS;
        siteIdToCourseMap = (HashMap) bun.getSerializable(getString(R.string.siteid_to_course_map));
        allAnnouncements = new ArrayList<>();


        if (siteIdIfSiteAnnouncements == null) {
            loadMoreListener = new LoadsAllAnnouncements();
            liveData = ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .getAllAnnouncements(NUM_ANNOUNCEMENTS_DEFAULT);

        } else {
            loadMoreListener = new LoadsSiteAnnouncements();

            liveData = ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .getSiteAnnouncements(siteIdIfSiteAnnouncements, NUM_ANNOUNCEMENTS_DEFAULT);
        }



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_announcements, null);
        announcementRecycler = view.findViewById(R.id.announcements_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        announcementRecycler.setLayoutManager(layoutManager);
        announcementRecycler.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setRefreshing(true);

        createAdapter();


        liveData.observe(getActivity(), announcements -> {

            addNewAnnouncementsToAdapter(announcements);
            swipeRefreshLayout.setRefreshing(false);
        });

        return view;
    }


    private void createAdapter() {

        adapter = new AnnouncementsAdapter(
                allAnnouncements,
                announcementRecycler,
                siteIdToCourseMap,
                announcementType
        );
        adapter.setClickListener(onAnnouncementSelected);
        adapter.setLoadMoreListener(loadMoreListener);

        announcementRecycler.setAdapter(adapter);


        //rerun animations for card entry
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_enter);
        announcementRecycler.setLayoutAnimation(controller);

        announcementRecycler.scheduleLayoutAnimation();


        swipeRefreshLayout.setOnRefreshListener(loadMoreListener::refresh);
    }


    /**
     * Adds the newly requested announcements to the adapter and notifies that
     * they were loaded
     *
     * @param newAnnouncements announcements to add
     */
    private void addNewAnnouncementsToAdapter(List<Announcement> newAnnouncements) {

        //remove the null element we had added to signify a loading item
        if (allAnnouncements.size() > 0)
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
            int numAnnouncementsToRequest = allAnnouncements.size() - 1 + ANNOUNCEMENTS_TO_GET_PER_REQUEST;

            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshAllAnnouncements(numAnnouncementsToRequest);

        }

        @Override
        public void refresh() {

            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshAllAnnouncements(NUM_ANNOUNCEMENTS_DEFAULT);
        }


    }

    /**
     * // TODO observe add new announcements to adapter
     * <p>
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
            announcementRecycler.post(() ->
                    adapter.notifyItemInserted(allAnnouncements.size() - 1)
            );


            int numAnnouncementsToRequest = allAnnouncements.size() - 1 + ANNOUNCEMENTS_TO_GET_PER_REQUEST;

            String siteId = allAnnouncements.get(0).siteId;

            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshSiteData(siteId, numAnnouncementsToRequest);

        }


        @Override
        public void refresh() {

            String siteId = allAnnouncements.get(0).siteId;

            ViewModelProviders.of(getActivity(), viewModelFactory)
                    .get(AnnouncementViewModel.class)
                    .refreshSiteData(siteId, NUM_ANNOUNCEMENTS_DEFAULT);
        }

    }


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        if (context instanceof Activity) {
            Activity activity = getActivity(context);

            try {
                onAnnouncementSelected = (OnAnnouncementSelected) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnAnnouncementSelected");
            }

        }
    }

    public Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }


}


