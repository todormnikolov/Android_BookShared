package com.tnt.android.android_bookshared.database;

import android.content.Context;
import android.database.Cursor;

import com.tnt.android.android_bookshared.common.Book;
import com.tnt.android.android_bookshared.common.User;


public class UserDbUtils {

    private static UserDbUtils instance;
    private UserDbHelper db;

    private UserDbUtils(Context context) {
        initDB(context);
    }

    public static UserDbUtils getInstance(Context context) {
        if (instance == null) {
            instance = new UserDbUtils(context);
        }
        return instance;
    }

    public void writeUserRecord(User user) {
        db.insertUser(user);
    }

    public void writeUserRecord(String username, double latitude, double longitude) {
        db.insertUser(username,latitude, longitude);
    }

    public void updateUserLocation(String username, double latitude, double longitude){
        db.updateUserLocation(username, latitude, longitude);
    }

    public Cursor readUserRecord() {
        return db.getUserValues();
    }

    public void writeBookRecord(Book book) {
        db.insertBook(book);
    }

    public void updateBook(int id, String username){
        db.updateBookCurrentOwner(id, username);
    }

    public Cursor readBookRecord() {
        return db.getBookValues();
    }

    private UserDbHelper initDB(Context context) {
        if (db == null) {
            db = new UserDbHelper(context);
        }
        return db;
    }
}
