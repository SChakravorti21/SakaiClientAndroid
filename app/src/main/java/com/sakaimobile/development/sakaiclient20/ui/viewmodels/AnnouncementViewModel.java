package com.sakaimobile.development.sakaiclient20.ui.viewmodels;


import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.repositories.AnnouncementRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementViewModel extends BaseViewModel {

    private AnnouncementRepository announcementRepository;
    private MutableLiveData<List<Announcement>> announcementsLiveData;

    @Inject
    AnnouncementViewModel(AnnouncementRepository repo) {
        super();
        announcementRepository = repo;
        announcementsLiveData = new MutableLiveData<>();
    }

    @SuppressLint("CheckResult")
    public void refreshSiteData(String siteId) {
        announcementRepository
                .refreshSiteAnnouncements(siteId)
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(this::emitError)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announcements -> {
                    if (announcements.isEmpty())
                        announcementsLiveData.setValue(null);
                    }, Throwable::printStackTrace
                );
    }

    @SuppressLint("CheckResult")
    public void refreshAllData() {
        announcementRepository
                .refreshAllAnnouncements()
                .doOnSubscribe(compositeDisposable::add)
                .doOnError(this::emitError)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announcements -> {
                    if (announcements.isEmpty())
                        announcementsLiveData.setValue(null);
                    }, Throwable::printStackTrace
                );
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
                                refreshAllData();
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
                                refreshSiteData(siteId);
                            else
                                announcementsLiveData.setValue(announcements);
                        }
                )
        );

        return announcementsLiveData;
    }
}
