package com.example.development.sakaiclient20.persistence.entities;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "announcements",
//        foreignKeys = @ForeignKey(entity = Course.class,
//                parentColumns = "siteId",
//                childColumns = "siteId",
//                onDelete = ForeignKey.CASCADE,
//                onUpdate = ForeignKey.CASCADE),
        indices = {
                @Index(value = "siteId"),
                @Index(value = "announcementId")
        }
)
// TODO: implement parcelable
public class Announcement  {

    @NonNull
    @PrimaryKey
    public final String announcementId;

    @Ignore
    public List<Attachment> attachments = new ArrayList<>();

    public String body;
    public String title;
    public String siteId;
    public String createdBy;
    public long createdOn;

    public Announcement(@NonNull String announcementId) {
        this.announcementId = announcementId;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeString(announcementId);
//        parcel.writeString(body);
//        parcel.writeString(title);
//        parcel.writeString(siteId);
//        parcel.writeString(createdBy);
//        parcel.writeLong(createdOn);
//        parcel.writeList(attachments);
//    }
}
