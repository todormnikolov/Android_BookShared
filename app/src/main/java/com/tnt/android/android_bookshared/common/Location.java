package com.tnt.android.android_bookshared.common;

/**
 * Created by USER on 11.7.2016 г..
 */
public class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
