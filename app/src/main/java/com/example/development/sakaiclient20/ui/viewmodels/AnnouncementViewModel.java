package com.example.development.sakaiclient20.ui.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.repositories.AnnouncementRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementViewModel extends ViewModel {

    private AnnouncementRepository announcementRepository;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // need to have two seperate ones, must observe on all announcements (When refresh all announcements tab)
    // and must observe on siteIdToAnnouncements when refresh site announcements
    private MutableLiveData<List<Announcement>> allAnnouncements;
    private Map<String, MutableLiveData<List<Announcement>>> siteIdToAnnouncements;


    @Inject
    AnnouncementViewModel(AnnouncementRepository repo) {
        announcementRepository = repo;
        siteIdToAnnouncements = new HashMap<>();
    }


    public LiveData<List<Announcement>>  getAllAnnouncements(int num) {
        if(allAnnouncements == null) {
            allAnnouncements = new MutableLiveData<>();
            refreshAllData(num);
        }

        return allAnnouncements;
    }

    public LiveData<List<Announcement>> getSiteAnnouncements(String siteId, int num) {

        if(!siteIdToAnnouncements.containsKey(siteId)) {
            siteIdToAnnouncements.put(siteId, new MutableLiveData<>());
            refreshSiteData(siteId, num);
        }

        return siteIdToAnnouncements.get(siteId);
    }


    /**
     * get all announcements from repository, set the value of allAnnouncements
     * and update the hashmap with all the new announcements
     */
    private void loadAllAnnouncements() {
        compositeDisposable.add(
                announcementRepository.getAllAnnouncements()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (announcements) -> {
                            allAnnouncements.setValue(announcements);
                            updateSiteAnnouncementsMap(announcements);
                        },
                        Throwable::printStackTrace
                )
        );
    }

    /**
     * Refresh all announcements
     */
    public void refreshAllData(int num) {

        compositeDisposable.add(
                announcementRepository.refreshAllAnnouncements(num)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::loadAllAnnouncements,
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
    private void loadSiteAnnouncements(String siteId) {
        compositeDisposable.add(
                announcementRepository.getSiteAnnouncements(siteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (siteAnnouncements) -> {
                                    siteIdToAnnouncements.get(siteId).setValue(siteAnnouncements);
                                    updateAllAnnouncements(siteAnnouncements);
                                },
                                Throwable::printStackTrace
                        )
        );
    }

    /**
     * Refresh all site announcements
     * @param siteId
     */
    public void refreshSiteData(String siteId, int num) {
        compositeDisposable.add(
                announcementRepository.refreshSiteAnnouncements(siteId, num)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> loadSiteAnnouncements(siteId),
                                Throwable::printStackTrace
                        )
        );
    }



    /**
     * Takes a list of all announcements and updates the hashmap
     * which stores siteId -> mutable live data of list of announcements
     *
     * @param announcements all announcements list
     */
    private void updateSiteAnnouncementsMap(List<Announcement> announcements) {

        Map<String, List<Announcement>> announcementsMap = new HashMap<>();
        for(Announcement announcement : announcements) {

            String siteId = announcement.siteId;
            if(!announcementsMap.containsKey(siteId)) {
                announcementsMap.put(siteId, new ArrayList<>());
            }

            announcementsMap.get(siteId).add(announcement);
        }

        // set the updated values
        for(String siteId : siteIdToAnnouncements.keySet()) {
            siteIdToAnnouncements.get(siteId).setValue(announcementsMap.get(siteId));
        }
    }


    /**
     * In the all announcements list, update all the announcements
     * that have the same siteId as the newly requested site announcements
     *
     * @param siteAnnouncements
     */
    private void updateAllAnnouncements(List<Announcement> siteAnnouncements) {
        if(allAnnouncements == null || allAnnouncements.getValue() == null)
            return;

        String siteId = siteAnnouncements.get(0).siteId;
        List<Announcement> allAnnouncementsList = new ArrayList<>();

        // remove all elements with the site id of the new announcements
        for(Announcement announcement : allAnnouncements.getValue()) {
            if(!announcement.siteId.equals(siteId))
                allAnnouncementsList.add(announcement);
        }

        // add all the new announcements
        allAnnouncementsList.addAll(siteAnnouncements);

        // set the new value of the mutable live data
        allAnnouncements.setValue(allAnnouncementsList);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
