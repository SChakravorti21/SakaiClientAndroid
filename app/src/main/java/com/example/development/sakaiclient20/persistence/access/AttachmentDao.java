package com.example.development.sakaiclient20.persistence.access;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.example.development.sakaiclient20.persistence.entities.Attachment;

@Dao
public abstract class AttachmentDao implements BaseDao<Attachment> {

    @Query("DELETE FROM attachments WHERE assignmentId = :assignmentId")
    public abstract void deleteAttachmentsForAssignment(String assignmentId);

}
