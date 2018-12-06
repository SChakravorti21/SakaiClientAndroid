package com.example.development.sakaiclient20.repositories;

import com.example.development.sakaiclient20.networking.services.AnnouncementsService;
import com.example.development.sakaiclient20.persistence.access.AnnouncementDao;
import com.example.development.sakaiclient20.persistence.access.AttachmentDao;
import com.example.development.sakaiclient20.persistence.entities.Announcement;

import java.util.List;

import io.reactivex.Single;

public class AnnouncementsRepository {

    private AnnouncementDao announcementDao;
    private AttachmentDao attachmentDao;
    private AnnouncementsService announcementsService;

    public AnnouncementsRepository(AnnouncementDao announcementDao, AttachmentDao attachmentDao, AnnouncementsService announcementsService) {
        this.announcementDao = announcementDao;
        this.attachmentDao = attachmentDao;
        this.announcementsService = announcementsService;
    }

//    public Single<List<Announcement>>
}
