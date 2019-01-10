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
    private static final int REQ_NUM_ANNOUNCEMENTS = 100000;


    // this is stateful, which means that it will keep track the next set of announcements
    // to return without additional parameters
    private static int START_INDEX = 0;
    private static final int NUM_ANNOUNCEMENTS_PER_SET = 30;


    public AnnouncementRepository(AnnouncementDao announcementDao, AttachmentDao attachmentDao, AnnouncementsService announcementsService) {
        this.announcementDao = announcementDao;
        this.attachmentDao = attachmentDao;
        this.announcementsService = announcementsService;
    }


    public Single<Integer> getNumAnnouncements() {
        return announcementDao.getAnnouncementCount();
    }

    public void resetAnnouncementsSetPosition() {
        START_INDEX = 0;
    }


    /**
     *
     * @return
     */
    public Single<List<Announcement>> getNextSetOfAllAnnouncements() {

        Single<List<Announcement>> announcements =
                announcementDao
                .getAllAnnouncementsInRange(START_INDEX, NUM_ANNOUNCEMENTS_PER_SET)
                .map(AnnouncementRepository::flattenCompositesToEntities)
                .firstOrError();

        // increment the start index so the next time we ask for
        // the next set of announcements, it will give the next ones
        START_INDEX += NUM_ANNOUNCEMENTS_PER_SET;

        return announcements;
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
        announcementDao.deleteAllAnnouncements();

        announcementDao.insert(announcements);
        for (Announcement announcement : announcements) {
            attachmentDao.insert(announcement.attachments);
        }

        return announcements;
    }
}
