package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.adapters.AnnouncementsAdapter;
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

    private FloatingActionButton scrollUpButton;


    // recycler view displaying announcements
    private RecyclerView announcementRecycler;
    // adapter which puts announcements into recycler view
    private AnnouncementsAdapter adapter;
    private HashMap<String, Course> siteIdToCourseMap; // needed for the adapter


    // announcement type (SITE or ALL)
    private int announcementType;

    private LiveData<List<Announcement>> announcementLiveData; // observe on it

    private ProgressBar spinner;

    private String announcementsSiteId;


    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // get arguments from bundle
        Bundle bun = getArguments();
        announcementsSiteId = bun.getString(getString(R.string.siteid_tag));
        announcementType = (announcementsSiteId == null) ? ALL_ANNOUNCEMENTS : SITE_ANNOUNCEMENTS;
        siteIdToCourseMap = (HashMap) bun.getSerializable(getString(R.string.siteid_to_course_map));
        allAnnouncements = new ArrayList<>();

        // setup the correct live data and loadMoreListener depending on
        // showing site or all announcements
        if (announcementType == ALL_ANNOUNCEMENTS) {
            announcementLiveData = announcementViewModel
                    .getAllAnnouncements();

        } else {
            announcementLiveData = announcementViewModel
                    .getSiteAnnouncements(announcementsSiteId);
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

        scrollUpButton = view.findViewById(R.id.scrollUpButton);

        createAdapter();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // each time the observation is triggered, we have new announcements (after refreshing)
        announcementLiveData.observe(getViewLifecycleOwner(), announcements -> {

            addNewAnnouncementsToAdapter(announcements);
            announcementRecycler.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
        });


        scrollUpButton.setOnClickListener((v) -> {
            announcementRecycler.getLayoutManager().smoothScrollToPosition(announcementRecycler, new RecyclerView.State(), 0);
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

                if(announcementType == SITE_ANNOUNCEMENTS)
                    announcementViewModel.refreshSiteAnnouncements(announcementsSiteId);
                else
                    announcementViewModel.refreshAllAnnouncements();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void createAdapter() {

        adapter = new AnnouncementsAdapter(
                allAnnouncements,
                siteIdToCourseMap,
                announcementRecycler,
                announcementType,
                scrollUpButton
        );
        adapter.setClickListener(this);

        announcementRecycler.setAdapter(adapter);


        //rerun animations for card entry
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_enter);
        announcementRecycler.setLayoutAnimation(controller);

        announcementRecycler.scheduleLayoutAnimation();
    }

    private void addNewAnnouncementsToAdapter(List<Announcement> announcements) {
        this.allAnnouncements.clear();
        allAnnouncements.addAll(announcements);
        adapter.notifyDataSetChanged();
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


