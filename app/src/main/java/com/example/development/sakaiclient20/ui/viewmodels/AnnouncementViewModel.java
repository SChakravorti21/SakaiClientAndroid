package com.example.development.sakaiclient20.ui.viewmodels;


import android.arch.lifecycle.MutableLiveData;

import com.example.development.sakaiclient20.persistence.entities.Announcement;
import com.example.development.sakaiclient20.repositories.AnnouncementRepository;
import com.example.development.sakaiclient20.repositories.CourseRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementViewModel extends BaseViewModel {

    private AnnouncementRepository announcementRepository;
    private MutableLiveData<List<Announcement>> announcements;

    @Inject
    public AnnouncementViewModel(CourseRepository courseRepository, AnnouncementRepository announcementRepository) {
        super(courseRepository);
        this.announcementRepository = announcementRepository;
        this.announcements = new MutableLiveData<>();
    }

    private void loadAllAnnouncements() {
        compositeDisposable.add(
                announcementRepository.getAllAnnouncements()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        announcements::setValue,
                        Throwable::printStackTrace
                )
        );
    }

    @Override
    void refreshAllData() {
        compositeDisposable.add(
                announcementRepository.refreshAllAnnouncements()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::loadAllAnnouncements,
                                Throwable::printStackTrace
                        )
        );
    }

//    private List<Announcement> clearStuff(List<Announcement> stuff) {
//        this.siteIdToAnnouncements.clear();
//        return stuff;
//    }
//
//    private void loadAllAnnouncements() {
//        compositeDisposable.add(
//                announcementRepository.getAllAnnouncements()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .toObservable()
//                .map(this::clearStuff)
//                .flatMapIterable(announcements -> announcements)
//
//        )
//    }


    /**
     * Loads site announcements from database into a mutable live data
     *
     * @param siteId siteId to get announcements for
     */
    private void loadSiteAnnouncements(String siteId) {
        compositeDisposable.add(
                announcementRepository.getSiteAnnouncements(siteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                announcements::setValue,
                                Throwable::printStackTrace
                        )
        );
    }


    @Override
    void refreshSiteData(String siteId) {
        compositeDisposable.add(
                announcementRepository.refreshSiteAnnouncements(siteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> loadSiteAnnouncements(siteId),
                                Throwable::printStackTrace
                        )
        );
    }
}
