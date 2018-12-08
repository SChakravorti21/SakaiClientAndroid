package com.example.development.sakaiclient20.repositories;

import com.example.development.sakaiclient20.models.sakai.announcements.AnnouncementsResponse;
import com.example.development.sakaiclient20.networking.services.AnnouncementsService;
import com.example.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.example.development.sakaiclient20.persistence.access.AttachmentDao;
import com.example.development.sakaiclient20.persistence.composites.AnnouncementWithAttachments;
import com.example.development.sakaiclient20.persistence.entities.Announcement;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public class AnnouncementRepository {

    private AnnouncementDao announcementDao;
    private AttachmentDao attachmentDao;
    private AnnouncementsService announcementsService;

    public AnnouncementRepository(AnnouncementDao announcementDao, AttachmentDao attachmentDao, AnnouncementsService announcementsService) {
        this.announcementDao = announcementDao;
        this.attachmentDao = attachmentDao;
        this.announcementsService = announcementsService;
    }

    public Single<List<Announcement>> getAllAnnouncements() {
        return announcementDao
                .getAllAnnouncements()
                .map(AnnouncementRepository::flattenCompositesToEntities)
                .firstOrError();
    }

    public Completable refreshAllAnnouncements() {
        return announcementsService
                .getAllAnnouncements(10000, 10000)
                .map(AnnouncementsResponse::getAnnouncements)
                .map(this::persistAnnouncements)
                .ignoreElement();
    }


    public Single<List<Announcement>> getSiteAnnouncements(String siteId) {
        return announcementDao
                .getAnnouncementsForSite(siteId)
                .map(AnnouncementRepository::flattenCompositesToEntities)
                .firstOrError();
    }

    public Completable refreshSiteAnnouncements(String siteId) {
        return announcementsService
                .getAnnouncementsForSite(siteId, 10000, 10000)
                .map(AnnouncementsResponse::getAnnouncements)
                .map(this::persistAnnouncements)
                .ignoreElement();
    }



    static List<Announcement> flattenCompositesToEntities(List<AnnouncementWithAttachments> announcementWithAttachments) {

        List<Announcement> announcements = new ArrayList<>(announcementWithAttachments.size());

        for(AnnouncementWithAttachments composite : announcementWithAttachments) {
            Announcement announcement = composite.announcement;
            announcement.attachments = composite.attachments;
            announcements.add(announcement);
        }

        return announcements;
    }

    private List<Announcement> persistAnnouncements(List<Announcement> announcements) {

        announcementDao.insert(announcements);
        for(Announcement announcement : announcements) {
            attachmentDao.insert(announcement.attachments);
        }

        return announcements;
    }
}
