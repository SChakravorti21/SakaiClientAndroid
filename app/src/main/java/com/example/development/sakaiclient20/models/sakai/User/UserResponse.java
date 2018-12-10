package com.example.development.sakaiclient20.models.sakai.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserResponse implements Serializable {

    @SerializedName("displayName")
    @Expose
    public String displayName;

    @SerializedName("email")
    @Expose
    public String email;

}
