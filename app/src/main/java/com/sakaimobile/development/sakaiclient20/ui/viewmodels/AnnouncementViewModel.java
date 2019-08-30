package com.sakaimobile.development.sakaiclient20.ui.viewmodels;


import android.annotation.SuppressLint;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.repositories.AnnouncementRepository;
import com.sakaimobile.development.sakaiclient20.repositories.CourseRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AnnouncementViewModel extends BaseViewModel {

    public static class AnnouncementsWithCourses {
        // Map from site IDs to course objects
        public List<Course> courses = null;
        public List<Announcement> announcements = null;
    }

    private AnnouncementRepository announcementRepository;
    private MutableLiveData<List<Announcement>> announcementsLiveData;

    @Inject
    AnnouncementViewModel(CourseRepository courseRepository,
                          AnnouncementRepository announcementRepository) {
        super(courseRepository);
        this.announcementRepository = announcementRepository;
        this.announcementsLiveData = new MutableLiveData<>();
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
        return getAnnouncementsMediatorLiveData(null);
    }

    public LiveData<List<Announcement>> getSiteAnnouncements(String siteId) {
        return getAnnouncementsMediatorLiveData(siteId);
    }

    private LiveData<List<Announcement>> getAllAnnouncementsLiveData() {
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

    private LiveData<List<Announcement>> getSiteAnnouncementsLiveData(String siteId) {
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

    private LiveData<List<Announcement>> getAnnouncementsMediatorLiveData(String siteId) {
        AnnouncementsWithCourses announcementsWithCourses = new AnnouncementsWithCourses();
        MediatorLiveData<List<Announcement>> mediatorLiveData = new MediatorLiveData<>();

        LiveData<List<Announcement>> announcementsLiveData = siteId == null
                ? getAllAnnouncementsLiveData()
                : getSiteAnnouncementsLiveData(siteId);

        mediatorLiveData.addSource(announcementsLiveData, announcements -> {
            announcementsWithCourses.announcements = announcements;
            attachCourseDataToAnnouncementsAndNotify(announcementsWithCourses, mediatorLiveData);
        });

        mediatorLiveData.addSource(getUnsortedCourses(), courses -> {
            announcementsWithCourses.courses = courses;
            attachCourseDataToAnnouncementsAndNotify(announcementsWithCourses, mediatorLiveData);
        });

        return mediatorLiveData;
    }

    private void attachCourseDataToAnnouncementsAndNotify(
            AnnouncementsWithCourses announcementsWithCourses,
            MediatorLiveData<List<Announcement>> mediatorLiveData) {
        if(announcementsWithCourses.announcements == null
                || announcementsWithCourses.courses == null) {
            return;
        }

        Map<String, Course> courseMap = new HashMap<>();
        for(Course course : announcementsWithCourses.courses) {
            courseMap.put(course.siteId, course);
        }

        for(Announcement announcement : announcementsWithCourses.announcements) {
            Course course = courseMap.get(announcement.siteId);
            announcement.subjectCode = course.subjectCode;
            announcement.courseTitle = course.title;
        }

        mediatorLiveData.setValue(announcementsWithCourses.announcements);
    }
}
