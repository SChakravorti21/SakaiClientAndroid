package com.sakaimobile.development.sakaiclient20.ui.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.networking.utilities.SharedPrefsUtil;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.ui.adapters.AnnouncementsAdapter;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.AnnouncementViewModel;
import com.sakaimobile.development.sakaiclient20.ui.viewmodels.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;

public class AnnouncementsFragment extends BaseFragment
        implements AnnouncementsAdapter.OnAnnouncementSelected {

    public static final int ALL_ANNOUNCEMENTS = 0;
    public static final int SITE_ANNOUNCEMENTS = 1;

    @Inject ViewModelFactory viewModelFactory;
    private AnnouncementViewModel announcementViewModel;
    private LiveData<List<Announcement>> announcementLiveData; // observe on it

    // announcement type (SITE or ALL)
    private int announcementType;
    private String announcementsSiteId; // null if announcementType == ALL
    private List<Announcement> announcements;

    private ProgressBar spinner;
    private AnnouncementsAdapter adapter;
    private RecyclerView announcementRecycler;
    private FloatingActionButton scrollUpButton;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // get arguments from bundle
        Bundle bun = getArguments();
        announcementsSiteId = bun.getString(getString(R.string.siteid_tag));
        announcementType = (announcementsSiteId == null) ? ALL_ANNOUNCEMENTS : SITE_ANNOUNCEMENTS;
        announcements = new ArrayList<>();

        // setup the correct live data and loadMoreListener depending on
        // showing site or all announcements
        announcementLiveData = announcementType == ALL_ANNOUNCEMENTS
                ? announcementViewModel.getAllAnnouncements()
                : announcementViewModel.getSiteAnnouncements(announcementsSiteId);

        // Now that we have gotten all the data we need, clear the bundle.
        // Otherwise, we'll probably get a runtime exception (TransactionTooLarge)
        // if the user attempts to open any announcement attachments.
        // It's fine to clear the bundle because onCreate will not be called again if
        // we return from a different application
        bun.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcements, null);

        // setup recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        announcementRecycler = view.findViewById(R.id.announcements_recycler);
        announcementRecycler.setLayoutManager(layoutManager);
        announcementRecycler.setItemAnimator(new DefaultItemAnimator());

        // start the spinner
        spinner = view.findViewById(R.id.progress_circular);
        spinner.setVisibility(View.VISIBLE);
        scrollUpButton = view.findViewById(R.id.scroll_up_button);

        // create the adapter which the recycler view will use to display announcements
        adapter = new AnnouncementsAdapter(announcements);
        adapter.setClickListener(this);
        announcementRecycler.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initRefreshFailureListener(announcementViewModel, () -> {
            spinner.setVisibility(View.GONE);
            announcementRecycler.setVisibility(View.VISIBLE);
            return null;
        });

        // each time the observation is triggered, we have new announcements (after refreshing)
        announcementLiveData.observe(getViewLifecycleOwner(), announcements -> {
            if(announcements == null) {
                Toast.makeText(getContext(), "No announcements found", Toast.LENGTH_SHORT).show();
                spinner.setVisibility(View.GONE);
                return;
            }

            scrollUpButton.show();
            announcementRecycler.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            replaceAnnouncements(announcements);
        });

        // grow/shrink the FAB when scrolling
        LinearLayoutManager manager = (LinearLayoutManager) announcementRecycler.getLayoutManager();
        scrollUpButton.setOnClickListener((v) -> {
            if(manager != null) {
                manager.smoothScrollToPosition(announcementRecycler, new RecyclerView.State(), 0);
            }
        });

        announcementRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // if the first item is visible then make the FAB disappear
                // or if the user is scrolling down
                if((manager != null && manager.findFirstCompletelyVisibleItemPosition() == 0)
                        || dy > 0) {
                    scrollUpButton.hide();
                    // otherwise make the FAB reappear
                } else {
                    scrollUpButton.show();
                }
            }
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        saveScrollState();
        announcementRecycler.clearOnScrollListeners();
        scrollUpButton.setOnClickListener(null);
        announcementRecycler = null;
        scrollUpButton = null;
        spinner = null;
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
                scrollUpButton.hide();
                // save the state so we can scroll to the right position after reloading
                saveScrollState();
                refreshAnnouncements();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void replaceAnnouncements(List<Announcement> announcements) {
        this.announcements.clear();
        this.announcements.addAll(announcements);
        adapter.notifyDataSetChanged();

        // restore scroll state
        if(announcementType == ALL_ANNOUNCEMENTS) {
            int pos = SharedPrefsUtil.getAnnouncementScrollState(getContext());
            if (pos >= 0 && pos < announcements.size())
                announcementRecycler.scrollToPosition(pos);
        }
    }

    @Override
    public void onAnnouncementSelected(Announcement announcement, View cardView, int position) {
        Bundle b = new Bundle();
        b.putSerializable(SingleAnnouncementFragment.SINGLE_ANNOUNCEMENT, announcement);
        b.putInt(SingleAnnouncementFragment.ANNOUNCEMENT_POSITION, position);

        SingleAnnouncementFragment announcementFragment = new SingleAnnouncementFragment();
        announcementFragment.setArguments(b);

        FragmentTransaction ft = getFragmentManager()
                .beginTransaction()
                .hide(this)
                .add(R.id.fragment_container, announcementFragment)
                .setReorderingAllowed(true)
                .addToBackStack(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Add second fragment by replacing first
            ft.addSharedElement(cardView, ViewCompat.getTransitionName(cardView));
        }

        ft.commit();
    }

    private void refreshAnnouncements() {
        if(announcementType == SITE_ANNOUNCEMENTS) {
            announcementViewModel.refreshSiteData(announcementsSiteId);
        } else {
            announcementViewModel.refreshAllData();
        }
    }

    private void saveScrollState() {
        Context context = getContext();
        LinearLayoutManager layoutManager = (LinearLayoutManager) announcementRecycler.getLayoutManager();
        if(announcementType == ALL_ANNOUNCEMENTS && context != null && layoutManager != null) {
            int currentPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
            SharedPrefsUtil.saveAnnouncementScrollState(context, currentPosition);
        }
    }
}


