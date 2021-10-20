package com.example.ecommerce.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Subhasmith Thapa on 20,October,2021
 */
public class Address {
    @SerializedName("location")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
