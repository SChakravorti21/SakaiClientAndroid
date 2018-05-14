package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import java.lang.Long;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SiteCollectionObject implements Serializable
{

    @SerializedName("contactEmail")
    @Expose
    private Object contactEmail;
    @SerializedName("contactName")
    @Expose
    private Object contactName;
    @SerializedName("createdDate")
    @Expose
    private Long createdDate;
    @SerializedName("createdTimeObject")
    @Expose
    private CreatedTimeObject createdTimeObject;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("htmlDescription")
    @Expose
    private String htmlDescription;
    @SerializedName("htmlShortDescription")
    @Expose
    private String htmlShortDescription;
    @SerializedName("iconUrl")
    @Expose
    private Object iconUrl;
    @SerializedName("iconUrlFull")
    @Expose
    private Object iconUrlFull;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("infoUrl")
    @Expose
    private Object infoUrl;
    @SerializedName("infoUrlFull")
    @Expose
    private Object infoUrlFull;
    @SerializedName("joinerRole")
    @Expose
    private Object joinerRole;
    @SerializedName("lastModified")
    @Expose
    private Long lastModified;
    @SerializedName("maintainRole")
    @Expose
    private String maintainRole;
    @SerializedName("modifiedDate")
    @Expose
    private Long modifiedDate;
    @SerializedName("modifiedTimeObject")
    @Expose
    private ModifiedTimeObject modifiedTimeObject;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("props")
    @Expose
    private PropsObject propsObject;
    @SerializedName("providerGroupId")
    @Expose
    private String providerGroupId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("shortDescription")
    @Expose
    private Object shortDescription;
    @SerializedName("siteGroups")
    @Expose
    private Object siteGroups;
    @SerializedName("siteOwnerObject")
    @Expose
    private SiteOwnerObject siteOwnerObject;
    @SerializedName("sitePageObjects")
    @Expose
    private List<SitePageObject> sitePageObjects = new ArrayList<SitePageObject>();
    @SerializedName("skin")
    @Expose
    private Object skin;
    @SerializedName("softlyDeletedDate")
    @Expose
    private Object softlyDeletedDate;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("userRoles")
    @Expose
    private List<String> userRoles = new ArrayList<String>();
    @SerializedName("activeEdit")
    @Expose
    private Boolean activeEdit;
    @SerializedName("customPageOrdered")
    @Expose
    private Boolean customPageOrdered;
    @SerializedName("empty")
    @Expose
    private Boolean empty;
    @SerializedName("joinable")
    @Expose
    private Boolean joinable;
    @SerializedName("pubView")
    @Expose
    private Boolean pubView;
    @SerializedName("published")
    @Expose
    private Boolean published;
    @SerializedName("softlyDeleted")
    @Expose
    private Boolean softlyDeleted;
    @SerializedName("entityReference")
    @Expose
    private String entityReference;
    @SerializedName("entityURL")
    @Expose
    private String entityURL;
    @SerializedName("entityId")
    @Expose
    private String entityId;
    @SerializedName("entityTitle")
    @Expose
    private String entityTitle;
    private final static long serialVersionUID = 3346237831791672351L;

    public Object getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(Object contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Object getContactName() {
        return contactName;
    }

    public void setContactName(Object contactName) {
        this.contactName = contactName;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public CreatedTimeObject getCreatedTimeObject() {
        return createdTimeObject;
    }

    public void setCreatedTimeObject(CreatedTimeObject createdTimeObject) {
        this.createdTimeObject = createdTimeObject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHtmlDescription() {
        return htmlDescription;
    }

    public void setHtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    public String getHtmlShortDescription() {
        return htmlShortDescription;
    }

    public void setHtmlShortDescription(String htmlShortDescription) {
        this.htmlShortDescription = htmlShortDescription;
    }

    public Object getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(Object iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Object getIconUrlFull() {
        return iconUrlFull;
    }

    public void setIconUrlFull(Object iconUrlFull) {
        this.iconUrlFull = iconUrlFull;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getInfoUrl() {
        return infoUrl;
    }

    public void setInfoUrl(Object infoUrl) {
        this.infoUrl = infoUrl;
    }

    public Object getInfoUrlFull() {
        return infoUrlFull;
    }

    public void setInfoUrlFull(Object infoUrlFull) {
        this.infoUrlFull = infoUrlFull;
    }

    public Object getJoinerRole() {
        return joinerRole;
    }

    public void setJoinerRole(Object joinerRole) {
        this.joinerRole = joinerRole;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getMaintainRole() {
        return maintainRole;
    }

    public void setMaintainRole(String maintainRole) {
        this.maintainRole = maintainRole;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public ModifiedTimeObject getModifiedTimeObject() {
        return modifiedTimeObject;
    }

    public void setModifiedTimeObject(ModifiedTimeObject modifiedTimeObject) {
        this.modifiedTimeObject = modifiedTimeObject;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public PropsObject getPropsObject() {
        return propsObject;
    }

    public void setPropsObject(PropsObject propsObject) {
        this.propsObject = propsObject;
    }

    public String getProviderGroupId() {
        return providerGroupId;
    }

    public void setProviderGroupId(String providerGroupId) {
        this.providerGroupId = providerGroupId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Object getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Object shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Object getSiteGroups() {
        return siteGroups;
    }

    public void setSiteGroups(Object siteGroups) {
        this.siteGroups = siteGroups;
    }

    public SiteOwnerObject getSiteOwnerObject() {
        return siteOwnerObject;
    }

    public void setSiteOwnerObject(SiteOwnerObject siteOwnerObject) {
        this.siteOwnerObject = siteOwnerObject;
    }

    public List<SitePageObject> getSitePageObjects() {
        return sitePageObjects;
    }

    public void setSitePageObjects(List<SitePageObject> sitePageObjects) {
        this.sitePageObjects = sitePageObjects;
    }

    public Object getSkin() {
        return skin;
    }

    public void setSkin(Object skin) {
        this.skin = skin;
    }

    public Object getSoftlyDeletedDate() {
        return softlyDeletedDate;
    }

    public void setSoftlyDeletedDate(Object softlyDeletedDate) {
        this.softlyDeletedDate = softlyDeletedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public Boolean getActiveEdit() {
        return activeEdit;
    }

    public void setActiveEdit(Boolean activeEdit) {
        this.activeEdit = activeEdit;
    }

    public Boolean getCustomPageOrdered() {
        return customPageOrdered;
    }

    public void setCustomPageOrdered(Boolean customPageOrdered) {
        this.customPageOrdered = customPageOrdered;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public Boolean getJoinable() {
        return joinable;
    }

    public void setJoinable(Boolean joinable) {
        this.joinable = joinable;
    }

    public Boolean getPubView() {
        return pubView;
    }

    public void setPubView(Boolean pubView) {
        this.pubView = pubView;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getSoftlyDeleted() {
        return softlyDeleted;
    }

    public void setSoftlyDeleted(Boolean softlyDeleted) {
        this.softlyDeleted = softlyDeleted;
    }

    public String getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(String entityReference) {
        this.entityReference = entityReference;
    }

    public String getEntityURL() {
        return entityURL;
    }

    public void setEntityURL(String entityURL) {
        this.entityURL = entityURL;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityTitle() {
        return entityTitle;
    }

    public void setEntityTitle(String entityTitle) {
        this.entityTitle = entityTitle;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("contactEmail", contactEmail).append("contactName", contactName).append("createdDate", createdDate).append("createdTimeObject", createdTimeObject).append("description", description).append("htmlDescription", htmlDescription).append("htmlShortDescription", htmlShortDescription).append("iconUrl", iconUrl).append("iconUrlFull", iconUrlFull).append("id", id).append("infoUrl", infoUrl).append("infoUrlFull", infoUrlFull).append("joinerRole", joinerRole).append("lastModified", lastModified).append("maintainRole", maintainRole).append("modifiedDate", modifiedDate).append("modifiedTimeObject", modifiedTimeObject).append("owner", owner).append("propsObject", propsObject).append("providerGroupId", providerGroupId).append("reference", reference).append("shortDescription", shortDescription).append("siteGroups", siteGroups).append("siteOwnerObject", siteOwnerObject).append("sitePageObjects", sitePageObjects).append("skin", skin).append("softlyDeletedDate", softlyDeletedDate).append("title", title).append("type", type).append("userRoles", userRoles).append("activeEdit", activeEdit).append("customPageOrdered", customPageOrdered).append("empty", empty).append("joinable", joinable).append("pubView", pubView).append("published", published).append("softlyDeleted", softlyDeleted).append("entityReference", entityReference).append("entityURL", entityURL).append("entityId", entityId).append("entityTitle", entityTitle).toString();
    }

}
