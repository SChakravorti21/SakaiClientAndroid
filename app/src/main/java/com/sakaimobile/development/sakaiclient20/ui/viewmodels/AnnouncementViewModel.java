package com.sakaimobile.development.sakaiclient20.ui.viewmodels;


import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.repositories.AnnouncementRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementViewModel extends ViewModel {

    private AnnouncementRepository announcementRepository;


    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<List<Announcement>> announcementsLiveData;


    @Inject
    AnnouncementViewModel(AnnouncementRepository repo) {
        announcementRepository = repo;
        announcementsLiveData = new MutableLiveData<>();
    }


    @SuppressLint("CheckResult")
    public LiveData<List<Announcement>> getNextSetOfAllAnnouncements() {

        // first check if the database is empty, if it is, must request
        // all announcements then load the first set into the live data
        announcementRepository.getNumAnnouncements()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(num -> {

                    if (num == 0)
                        refreshAllAnnouncements();
                    else
                        loadNextSetOfAnnouncements();

                });

        return announcementsLiveData;

    }


//    public LiveData<List<Announcement>> getSiteAnnouncements(String siteId, int num) {
//
//        // if the live data is null, initialize it, and try loading data
//        if (announcementsLiveData == null) {
//            announcementsLiveData = new MutableLiveData<>();
//            loadSiteAnnouncements(siteId, num);
//            announcementsSiteId = siteId;
//        }
//        // if the stored data is not for all announcements
//        else if(!announcementsSiteId.equals(siteId)) {
//            loadAllAnnouncements(num);
//            announcementsSiteId = siteId;
//        }
//
//        // correct live data
//        return announcementsLiveData;
//    }

    @SuppressLint("CheckResult")
    private void loadNextSetOfAnnouncements() {

        announcementRepository
                .getNextSetOfAllAnnouncements()
                .delay(1, TimeUnit.SECONDS)
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        announcements -> {
                            announcementsLiveData.setValue(announcements);
                        }
                );


    }


    /**
     * Refresh all announcements
     */
    public void refreshAllAnnouncements() {
        compositeDisposable.add(
                announcementRepository.refreshAllAnnouncements()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                (announcements) -> {
                                    if (announcements.size() == 0)
                                        announcementsLiveData.setValue(null);
                                    else {
                                        // reset the position since we are refreshing them all
                                        announcementRepository.resetAnnouncementsSetPosition();
                                        loadNextSetOfAnnouncements();
                                    }
                                },
                                Throwable::printStackTrace
                        )
        );
    }

//
//    /**
//     * Loads site announcements from database into the hashmap
//     * also update the all announcements list
//     *
//     * @param siteId siteId to get announcements for
//     */
//    private void loadSiteAnnouncements(String siteId, int num) {
//        compositeDisposable.add(
//                announcementRepository.getSiteAnnouncements(siteId)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                siteAnnouncements -> {
//                                    // if nothing in DB, refresh
//                                    if (siteAnnouncements.isEmpty())
//                                        refreshSiteData(siteId, num);
//                                    else
//                                        announcementsLiveData.setValue(siteAnnouncements);
//                                },
//                                Throwable::printStackTrace
//                        )
//        );
////    }
////
//    /**
//     * Refresh all site announcements
//     *
//     * @param siteId
//     */
//    public void refreshSiteData(String siteId, int num) {
//        compositeDisposable.add(
//                announcementRepository.refreshSiteAnnouncements(siteId, num)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                                announcements -> {
//                                    if (announcements.size() == 0)
//                                        announcementsLiveData.setValue(null);
//                                    else
//                                        loadSiteAnnouncements(siteId, num);
//                                },
//                                Throwable::printStackTrace
//                        )
//        );
//    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
        this.announcementRepository.resetAnnouncementsSetPosition();
    }
}
