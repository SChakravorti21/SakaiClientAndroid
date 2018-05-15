package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import java.lang.Long;

import com.example.development.sakaiclientandroid.models.SitePage;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SitePageObject implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("layout")
    @Expose
    private Long layout;
    @SerializedName("layoutTitle")
    @Expose
    private String layoutTitle;
    @SerializedName("position")
    @Expose
    private Long position;
    @SerializedName("props")
    @Expose
    private SitePagePropsObject props;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("siteId")
    @Expose
    private String siteId;
    @SerializedName("skin")
    @Expose
    private String skin;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("titleCustom")
    @Expose
    private Boolean titleCustom;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("activeEdit")
    @Expose
    private Boolean activeEdit;
    @SerializedName("homePage")
    @Expose
    private Boolean homePage;
    @SerializedName("popUp")
    @Expose
    private Boolean popUp;
    private final static long serialVersionUID = 4988978713324185542L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLayout() {
        return layout;
    }

    public void setLayout(Long layout) {
        this.layout = layout;
    }

    public String getLayoutTitle() {
        return layoutTitle;
    }

    public void setLayoutTitle(String layoutTitle) {
        this.layoutTitle = layoutTitle;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public SitePagePropsObject getProps() {
        return props;
    }

    public void setProps(SitePagePropsObject props) {
        this.props = props;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getTitleCustom() {
        return titleCustom;
    }

    public void setTitleCustom(Boolean titleCustom) {
        this.titleCustom = titleCustom;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getActiveEdit() {
        return activeEdit;
    }

    public void setActiveEdit(Boolean activeEdit) {
        this.activeEdit = activeEdit;
    }

    public Boolean getHomePage() {
        return homePage;
    }

    public void setHomePage(Boolean homePage) {
        this.homePage = homePage;
    }

    public Boolean getPopUp() {
        return popUp;
    }

    public void setPopUp(Boolean popUp) {
        this.popUp = popUp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("layout", layout).append("layoutTitle", layoutTitle).append("position", position).append("props", props).append("reference", reference).append("siteId", siteId).append("skin", skin).append("title", title).append("titleCustom", titleCustom).append("url", url).append("activeEdit", activeEdit).append("homePage", homePage).append("popUp", popUp).toString();
    }

}
