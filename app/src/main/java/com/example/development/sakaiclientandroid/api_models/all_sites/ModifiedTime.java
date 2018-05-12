package com.example.development.sakaiclientandroid.api_models.all_sites;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ModifiedTime implements Serializable
{

    @SerializedName("display")
    @Expose
    private String display;
    @SerializedName("time")
    @Expose
    private Integer time;
    private final static long serialVersionUID = -408072053877102379L;

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("display", display).append("time", time).toString();
    }

}
