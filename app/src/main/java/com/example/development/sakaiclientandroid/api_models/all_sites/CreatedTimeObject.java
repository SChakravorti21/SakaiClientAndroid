package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import java.lang.Long;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class CreatedTimeObject implements Serializable
{

    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("time")
    @Expose
    private Long time;
    private final static long serialVersionUID = 5498651416981472923L;

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("display", display).append("time", time).toString();
    }

}
