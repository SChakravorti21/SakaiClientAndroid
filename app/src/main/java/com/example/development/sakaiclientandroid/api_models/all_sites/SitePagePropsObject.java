package com.example.development.sakaiclientandroid.api_models.all_sites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by Development on 5/15/18.
 */

public class SitePagePropsObject implements Serializable {

    @SerializedName("is_home_page")
    @Expose
    private String isHomePage;

    public String getIsHomePage() {
        return isHomePage;
    }

    public void setIsHomePage(String isHomePage) {
        this.isHomePage = isHomePage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("is_home_page", isHomePage).toString();
    }
}
