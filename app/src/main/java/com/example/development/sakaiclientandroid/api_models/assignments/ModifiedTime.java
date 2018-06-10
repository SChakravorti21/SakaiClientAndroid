package com.example.development.sakaiclientandroid.api_models.assignments;

import java.io.Serializable;
import java.math.BigInteger;
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
    private BigInteger time;
    private final static long serialVersionUID = 7282929703156223305L;

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public BigInteger getTime() {
        return time;
    }

    public void setTime(BigInteger time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("display", display).append("time", time).toString();
    }

}
