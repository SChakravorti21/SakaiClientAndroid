package com.sakaimobile.development.sakaiclient20.persistence.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "resources")
public class Resource implements Serializable {

    // url to download file
    @NonNull
    @PrimaryKey
    public String url;

    // name of file
    public String title;

    // path to parent container, showing directory structure
    public String container;


    // if the resource element is a directory or file
    public boolean isDirectory;

    // number of children files, if a directory
    public int numChildren;

    // type of type, pdf, img ..., or directory (collection)
    public String type;

    // ID of site that this belongs to
    public String siteId;

    public Resource(String url) {
        this.url = url;
    }

}
