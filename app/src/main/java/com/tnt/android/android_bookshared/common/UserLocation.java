package com.tnt.android.android_bookshared.common;

/**
 * Created by USER on 11.7.2016 г..
 */
public class UserLocation {

    private String username;
    private float latitude;
    private float longitude;

    public UserLocation(String username) {
       setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
