package com.sakaimobile.development.sakaiclient20.ui.viewmodels;


import android.annotation.SuppressLint;
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


    @Inject
    AnnouncementViewModel(AnnouncementRepository repo) {
        announcementRepository = repo;
        announcementsLiveData = new MutableLiveData<>();
    }


    @SuppressLint("CheckResult")
    public void refreshSiteAnnouncements(String siteId) {
        announcementRepository
                .refreshSiteAnnouncements(siteId)
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announcements -> {
                    if (announcements.isEmpty())
                        announcementsLiveData.setValue(null);
                });
    }

    @SuppressLint("CheckResult")
    public void refreshAllAnnouncements() {
        announcementRepository
                .refreshAllAnnouncements()
                .doOnSubscribe(compositeDisposable::add)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announcements -> {
                    if (announcements.isEmpty())
                        announcementsLiveData.setValue(null);
                });
    }


    public LiveData<List<Announcement>> getAllAnnouncements() {
        compositeDisposable.add(
                announcementRepository
                        .getAllAnnouncements()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                announcements -> {
                                    if (announcements.isEmpty())
                                        refreshAllAnnouncements();
                                    else
                                        announcementsLiveData.setValue(announcements);
                                }
                        )
        );

        return announcementsLiveData;
    }


    public LiveData<List<Announcement>> getSiteAnnouncements(String siteId) {
        compositeDisposable.add(
                announcementRepository
                        .getSiteAnnouncements(siteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                announcements -> {
                                    if (announcements.isEmpty())
                                        refreshSiteAnnouncements(siteId);
                                    else
                                        announcementsLiveData.setValue(announcements);
                                }
                        )
        );

        return announcementsLiveData;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
