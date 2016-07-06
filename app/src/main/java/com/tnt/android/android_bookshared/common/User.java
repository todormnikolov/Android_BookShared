package com.tnt.android.android_bookshared.common;

/**
 * Created by USER on 30.6.2016 г..
 */
public class User {

    private String username;
    private String password;
    private String name;

    public User(){

    }

    public User(String username, String password, String name) {
        setUsername(username);
        setPassword(password);
        setName(name);
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
}
