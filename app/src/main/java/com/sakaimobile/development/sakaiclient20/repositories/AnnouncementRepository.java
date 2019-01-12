package com.sakaimobile.development.sakaiclient20.repositories;

import com.sakaimobile.development.sakaiclient20.models.sakai.announcements.AnnouncementsResponse;
import com.sakaimobile.development.sakaiclient20.networking.services.AnnouncementsService;
import com.sakaimobile.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.sakaimobile.development.sakaiclient20.persistence.access.AttachmentDao;
import com.sakaimobile.development.sakaiclient20.persistence.composites.AnnouncementWithAttachments;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Announcement;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

public class AnnouncementRepository {

    private AnnouncementDao announcementDao;
    private AttachmentDao attachmentDao;
    private AnnouncementsService announcementsService;

    private static final int REQ_DAYS_BACK = 10000;
    private static final int REQ_NUM_ANNOUNCEMENTS = 10000;


    private static final int NUM_ANNOUNCEMENTS_PER_SET = 10;


    public AnnouncementRepository(AnnouncementDao announcementDao, AttachmentDao attachmentDao, AnnouncementsService announcementsService) {
        this.announcementDao = announcementDao;
        this.attachmentDao = attachmentDao;
        this.announcementsService = announcementsService;
    }


    public Single<Integer> getNumAnnouncements() {
        return announcementDao.getAnnouncementCount();
    }


    /**
     * The view model will call this method to increment its start index
     * This method was created since I didn't want the view model to know anything about
     * the number of announcements in a set, its abstracted away
     *
     * @param startIndex view models start index in the announcement database
     * @return index will now start at the beginning of the next set
     */
    public static int incrementStartIndex(int startIndex) {
        return startIndex + NUM_ANNOUNCEMENTS_PER_SET;
    }

    /**
     * @return
     */
    public Single<List<Announcement>> getNextSetAllAnnouncements(int startIndex) {

        return announcementDao
                .getAllAnnouncementsInRange(startIndex, NUM_ANNOUNCEMENTS_PER_SET)
                .map(AnnouncementRepository::flattenCompositesToEntities)
                .firstOrError();

    }

    public Single<List<Announcement>> refreshAllAnnouncements() {
        return announcementsService
                .getAllAnnouncements(REQ_DAYS_BACK, REQ_NUM_ANNOUNCEMENTS)
                .map(AnnouncementsResponse::getAnnouncements)
                .map(this::persistAnnouncements);
    }


//    public Single<List<Announcement>> getSiteAnnouncements(String siteId) {
//        return announcementDao
//                .getSiteAnnouncements(siteId)
//                .map(AnnouncementRepository::flattenCompositesToEntities)
//                .firstOrError();
//    }
//
//    public Single<List<Announcement>> refreshSiteAnnouncements(String siteId, int num) {
//        return announcementsService
//                .getAnnouncementsForSite(siteId, REQ_DAYS_BACK, REQ_NUM_ANNOUNCEMENTS)
//                .map(AnnouncementsResponse::getAnnouncements)
//                .map(this::persistAnnouncements);
//    }


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

        // delete all announcements from the previous session
        // they are outdated!!!!!!!!!
        // announcementDao.deleteAllAnnouncements();

        announcementDao.insert(announcements);
        for (Announcement announcement : announcements) {
            attachmentDao.insert(announcement.attachments);
        }

        return announcements;
    }
}
