package com.sakaimobile.development.sakaiclient20.repositories;

import android.arch.lifecycle.LiveData;

import com.sakaimobile.development.sakaiclient20.models.sakai.announcements.AnnouncementsResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.AnnouncementsService;
import com.sakaimobile.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.composites.AnnouncementWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class AnnouncementRepository {

    private AnnouncementDao announcementDao;
    private AttachmentDao attachmentDao;
    private AnnouncementsService announcementsService;

    private static final int REQ_DAYS_BACK = 10000;
    private static final int REQ_NUM_ANNOUNCEMENTS = 10000;


    public AnnouncementRepository(AnnouncementDao announcementDao, AttachmentDao attachmentDao, AnnouncementsService announcementsService) {
        this.announcementDao = announcementDao;
        this.attachmentDao = attachmentDao;
        this.announcementsService = announcementsService;
    }


    public Single<List<Announcement>> getAllAnnouncements() {
        return announcementDao
                .getAllAnnouncements()
                .debounce(100, TimeUnit.MILLISECONDS)
                .map(AnnouncementRepository::flattenCompositesToEntities)
                .firstOrError();
    }


    public Single<List<Announcement>> getSiteAnnouncements(String siteId) {
        return announcementDao
                .getSiteAnnouncements(siteId)
                .debounce(100, TimeUnit.MILLISECONDS)
                .map(AnnouncementRepository::flattenCompositesToEntities)
                .firstOrError();
    }


    // refresh
    public Single<List<Announcement>> refreshAllAnnouncements() {
        return announcementsService
                .getAllAnnouncements(REQ_DAYS_BACK, REQ_NUM_ANNOUNCEMENTS)
                .map(AnnouncementsResponse::getAnnouncements)
                .map(this::persistAnnouncements);
    }

    public Single<List<Announcement>> refreshSiteAnnouncements(String siteId) {
        return announcementsService
                .getSiteAnnouncements(siteId, REQ_DAYS_BACK, REQ_NUM_ANNOUNCEMENTS)
                .map(AnnouncementsResponse::getAnnouncements)
                .map(this::persistAnnouncements);
    }


    static List<Announcement> flattenCompositesToEntities(List<AnnouncementWithAttachments> announcementWithAttachments) {

        List<Announcement> announcements = new ArrayList<>(announcementWithAttachments.size());

        for (AnnouncementWithAttachments composite : announcementWithAttachments) {
            Announcement announcement = composite.announcement;
            announcement.attachments = composite.attachments;
            announcements.add(announcement);
        }

        return announcements;
    }

    private List<Announcement> persistAnnouncements(List<Announcement> announcements) {

        // inserting the same announcements should delete the old ones
        // since both will have the same announcementID
        announcementDao.insert(announcements);

        // same case with its attachments
        for (Announcement announcement : announcements) {
            attachmentDao.insert(announcement.attachments);
        }

        return announcements;
    }
}
