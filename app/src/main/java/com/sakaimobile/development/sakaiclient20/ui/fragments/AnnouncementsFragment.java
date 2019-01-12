package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
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
import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class AnnouncementsFragment extends Fragment implements OnAnnouncementSelected {

    @Inject
    ViewModelFactory viewModelFactory;

    private AnnouncementViewModel announcementViewModel;

    public static final int ALL_ANNOUNCEMENTS = 0;
    public static final int SITE_ANNOUNCEMENTS = 1;

    // announcements to display
    private List<Announcement> allAnnouncements;


    // recycler view displaying announcements
    private RecyclerView announcementRecycler;
    // adapter which puts announcements into recycler view
    private AnnouncementsAdapter adapter;
    private HashMap<String, Course> siteIdToCourseMap; // needed for the adapter


    // loads more announcements and refreshes
    private LoadMoreListener loadMoreListener;

    // announcement type (SITE or ALL)
    private int announcementType;

    // whether or not there are more announcements to load
    private boolean hasLoadedAllAnnouncements = false;

    private LiveData<List<Announcement>> announcementLiveData; // observe on it

    private ProgressBar spinner;


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // get arguments from bundle
        Bundle bun = getArguments();
        String siteId = bun.getString(getString(R.string.siteid_tag));
        announcementType = (siteId == null) ? ALL_ANNOUNCEMENTS : SITE_ANNOUNCEMENTS;
        siteIdToCourseMap = (HashMap) bun.getSerializable(getString(R.string.siteid_to_course_map));
        allAnnouncements = new ArrayList<>();

        // setup the correct live data and loadMoreListener depending on
        // showing site or all announcements
        if (announcementType == ALL_ANNOUNCEMENTS) {
            loadMoreListener = new LoadsAllAnnouncements();
            announcementLiveData = announcementViewModel
                    .getNextSetOfAllAnnouncements();

        } else {
//            loadMoreListener = new LoadsSiteAnnouncements();
//            announcementLiveData = announcementViewModel
//                    .getSiteAnnouncements(siteId, NUM_ANNOUNCEMENTS_DEFAULT);
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_announcements, null);

        // setup recycler view
        announcementRecycler = view.findViewById(R.id.announcements_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        announcementRecycler.setLayoutManager(layoutManager);
        announcementRecycler.setItemAnimator(new DefaultItemAnimator());

        // start the spinner
        spinner = view.findViewById(R.id.progress_circular);
        spinner.setVisibility(View.VISIBLE);

        createAdapter();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // each time the observation is triggered, we have gotten the next set of announcements
        announcementLiveData.observe(getViewLifecycleOwner(), announcements -> {

            addNewAnnouncementsToAdapter(announcements);
            announcementRecycler.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                announcementRecycler.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                loadMoreListener.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createAdapter() {

        adapter = new AnnouncementsAdapter(
                allAnnouncements,
                announcementRecycler,
                siteIdToCourseMap,
                announcementType
        );
        adapter.setClickListener(this);
        adapter.setLoadMoreListener(loadMoreListener);

        announcementRecycler.setAdapter(adapter);


        //rerun animations for card entry
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_enter);
        announcementRecycler.setLayoutAnimation(controller);

        announcementRecycler.scheduleLayoutAnimation();
    }


    /**
     * Adds the newly gotten announcements to the adapter and notifies that
     * they were loaded
     *
     * @param newAnnouncements announcements to add
     */
    private void addNewAnnouncementsToAdapter(List<Announcement> newAnnouncements) {

        //remove the null element we had added to signify a loading item
        if (allAnnouncements.size() > 0)
            allAnnouncements.remove(allAnnouncements.size() - 1);

        if (newAnnouncements.size() == 0) {
            // if no more announcements to display, remove the loading bar
            adapter.finishedLoading();
            adapter.notifyItemRemoved(allAnnouncements.size());
            Toast.makeText(getContext(), getString(R.string.no_announcements), Toast.LENGTH_SHORT).show();
            hasLoadedAllAnnouncements = true;
        } else {
            // if there are new announcements add them and then update adapter
            int initialSize = allAnnouncements.size();

            // add all the new announcements
            allAnnouncements.addAll(newAnnouncements);

            // notify the adapter that we added some new items, so it can display
            adapter.notifyItemRangeChanged(initialSize, newAnnouncements.size());
            adapter.finishedLoading();
        }

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

            announcementViewModel.getNextSetOfAllAnnouncements();

        }

        @Override
        public void refresh() {
            // clear our current announcements and rerequest more
            allAnnouncements.clear();
            adapter.notifyDataSetChanged();
            announcementViewModel.refreshAllAnnouncements();
        }


    }

    /**
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


//            int numAnnouncementsToRequest = allAnnouncements.size() - 1 + ANNOUNCEMENTS_TO_GET_PER_REQUEST;

            String siteId = allAnnouncements.get(0).siteId;

//            announcementViewModel.refreshSiteData(siteId, numAnnouncementsToRequest);

        }


        @Override
        public void refresh() {

            String siteId = allAnnouncements.get(0).siteId;

//            announcementViewModel.refreshSiteData(siteId, NUM_ANNOUNCEMENTS_DEFAULT);
        }

    }


    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);

        // setup the view model
        announcementViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(AnnouncementViewModel.class);
    }

    @Override
    public void onAnnouncementSelected(Announcement announcement, Map<String, Course> siteIdToCourse) {
        Bundle b = new Bundle();
        b.putSerializable(getString(R.string.single_announcement_tag), announcement);
        // for some reason map isn't serializable, so i had to cast to hashmap
        b.putSerializable(getString(R.string.siteid_to_course_map), (HashMap) siteIdToCourse);

        SingleAnnouncementFragment fragment = new SingleAnnouncementFragment();
        fragment.setArguments(b);

        // load fragment
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.grow_enter, R.anim.pop_exit, R.anim.pop_enter, R.anim.pop_exit)
                .addToBackStack(null)
                .add(R.id.fragment_container, fragment)
                .commit();
    }
}


