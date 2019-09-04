package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.announcements.AnnouncementsResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.AnnouncementsService;
import com.sakaimobile.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.composites.AnnouncementWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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


    public Flowable<List<Announcement>> getAllAnnouncements() {
        return announcementDao
                .getAllAnnouncements()
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(AnnouncementRepository::flattenCompositesToEntities);
    }


    public Flowable<List<Announcement>> getSiteAnnouncements(String siteId) {
        return announcementDao
                .getSiteAnnouncements(siteId)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(AnnouncementRepository::flattenCompositesToEntities);
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

    /**
     * Persist the list of announcements gotten from network request in Room DB
     * this DB is observed on ,so after persisting the view model should receive the update
     * @param announcements list of new announcements to persist
     * @return persisted announcements
     */
    private List<Announcement> persistAnnouncements(List<Announcement> announcements) {
        // Construct a single list of attachments to insert since bulk insert is much faster
        List<Attachment> allAttachments = new ArrayList<>();
        for (Announcement announcement : announcements)
            allAttachments.addAll(announcement.attachments);

        // insert announcements and attachments if they are new, update otherwise
        announcementDao.upsert(announcements);
        attachmentDao.upsert(allAttachments);

        return announcements;
    }
}

