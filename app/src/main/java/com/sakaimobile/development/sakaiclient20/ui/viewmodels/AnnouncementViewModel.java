package com.sakaimobile.development.sakaiclient20.ui.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.repositories.AnnouncementRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementViewModel extends ViewModel {

    private AnnouncementRepository announcementRepository;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<List<Announcement>> announcementsLiveData;
    // the site ID of the announcements in the live data
    // if contains all announcements, the siteID will be a blank string
    // if the live data is empty, siteID will be null
    private String announcementsSiteId;

    @Inject
    AnnouncementViewModel(AnnouncementRepository repo) {
        announcementRepository = repo;
    }


    public LiveData<List<Announcement>> getAllAnnouncements(int num) {
        // if the live data is null, initialize it, and try loading data
        if (announcementsLiveData == null) {
            announcementsLiveData = new MutableLiveData<>();
            loadAllAnnouncements(num);
            announcementsSiteId = "";
        }
        // if the stored data is not for all announcements
        else if(!announcementsSiteId.equals("")) {
            loadAllAnnouncements(num);
            announcementsSiteId = "";
        }

        // correct live data
        return announcementsLiveData;
    }

    public LiveData<List<Announcement>> getSiteAnnouncements(String siteId, int num) {

        // if the live data is null, initialize it, and try loading data
        if (announcementsLiveData == null) {
            announcementsLiveData = new MutableLiveData<>();
            loadSiteAnnouncements(siteId, num);
            announcementsSiteId = siteId;
        }
        // if the stored data is not for all announcements
        else if(!announcementsSiteId.equals(siteId)) {
            loadAllAnnouncements(num);
            announcementsSiteId = siteId;
        }

        // correct live data
        return announcementsLiveData;
    }


    /**
     * get all announcements from repository, set the value of allAnnouncements
     * and update the hashmap with all the new announcements
     */
    private void loadAllAnnouncements(int num) {
        compositeDisposable.add(
                announcementRepository.getAllAnnouncements()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                announcements -> {
                                    // if nothing in DB, refresh
                                    if (announcements.isEmpty())
                                        refreshAllAnnouncements(num);
                                    else
                                        announcementsLiveData.setValue(announcements);
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    /**
     * Refresh all announcements
     */
    public void refreshAllAnnouncements(int num) {
        compositeDisposable.add(
                announcementRepository.refreshAllAnnouncements(num)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (announcements) -> {
                                    if (announcements.size() == 0)
                                        announcementsLiveData.setValue(null);
                                    else
                                        loadAllAnnouncements(num);
                                },
                                Throwable::printStackTrace
                        )
        );
    }


    /**
     * Loads site announcements from database into the hashmap
     * also update the all announcements list
     *
     * @param siteId siteId to get announcements for
     */
    private void loadSiteAnnouncements(String siteId, int num) {
        compositeDisposable.add(
                announcementRepository.getSiteAnnouncements(siteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                siteAnnouncements -> {
                                    // if nothing in DB, refresh
                                    if (siteAnnouncements.isEmpty())
                                        refreshSiteData(siteId, num);
                                    else
                                        announcementsLiveData.setValue(siteAnnouncements);
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    /**
     * Refresh all site announcements
     *
     * @param siteId
     */
    public void refreshSiteData(String siteId, int num) {
        compositeDisposable.add(
                announcementRepository.refreshSiteAnnouncements(siteId, num)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                announcements -> {
                                    if (announcements.size() == 0)
                                        announcementsLiveData.setValue(null);
                                    else
                                        loadSiteAnnouncements(siteId, num);
                                },
                                Throwable::printStackTrace
                        )
        );
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
