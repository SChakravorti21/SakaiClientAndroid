package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SiteOwner implements Serializable
{

    @SerializedName("userDisplayName")
    @Expose
    private String userDisplayName;
    @SerializedName("userEntityURL")
    @Expose
    private String userEntityURL;
    @SerializedName("userId")
    @Expose
    private String userId;
    private final static long serialVersionUID = 2203831453507075217L;

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserEntityURL() {
        return userEntityURL;
    }

    public void setUserEntityURL(String userEntityURL) {
        this.userEntityURL = userEntityURL;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("userDisplayName", userDisplayName).append("userEntityURL", userEntityURL).append("userId", userId).toString();
    }

}
