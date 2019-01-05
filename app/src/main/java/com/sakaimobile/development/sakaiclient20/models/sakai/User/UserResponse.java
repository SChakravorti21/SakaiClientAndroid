package com.sakaimobile.development.sakaiclient20.models.sakai.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserResponse implements Serializable {

    // Display name is the user's NetID,
    // only used to check if cookies are still valid
    @SerializedName("displayId")
    @Expose
    public String displayId;

}
