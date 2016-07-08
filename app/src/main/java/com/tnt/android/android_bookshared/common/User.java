package com.tnt.android.android_bookshared.common;

/**
 * Created by USER on 30.6.2016 г..
 */
public class User {

    private String username;
    private String password;
    private String name;
    private float latitude;
    private float longitude;

    public User(){

    }

    public User(String username, String password, String name) {
        setUsername(username);
        setPassword(password);
        setName(name);
    }

    public User(String username, String password, String name, float latitude, float longitude) {
        setUsername(username);
        setPassword(password);
        setName(name);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
