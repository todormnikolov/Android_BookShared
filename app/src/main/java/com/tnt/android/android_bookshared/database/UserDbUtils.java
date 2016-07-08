package com.tnt.android.android_bookshared.database;

import android.content.Context;
import android.database.Cursor;

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

    public Cursor readUserRecord() {
        return db.getUserValues();
    }

    private UserDbHelper initDB(Context context) {
        if (db == null) {
            db = new UserDbHelper(context);
        }
        return db;
    }
}
