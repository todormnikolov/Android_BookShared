package com.tnt.android.android_bookshared.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tnt.android.android_bookshared.common.Book;
import com.tnt.android.android_bookshared.common.User;

public class UserDbHelper extends SQLiteOpenHelper {

    protected SQLiteDatabase database;

    //Constants for User table
    public static final String DB_NAME = "db_users";
    public static final int DB_VERSION = 17;
    public static final String DB_TABLE_USERS = "DB_TABLE_USERS";
    public static final String KEY_USERNAME = "USERNAME";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";

    //Constants for Book table
    public static final String DB_TABLE_BOOKS = "DB_TABLE_BOOKS";
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_AUTHOR = "AUTHOR";
    public static final String KEY_ORIGINAL_OWNER = "ORIGINAL_OWNER";
    public static final String KEY_CURRENT_OWNER = "CURRENT_OWNER";

    public UserDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        open();
    }

    public void insertUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USERNAME, user.getUsername());
        cv.put(KEY_LATITUDE, user.getLatitude());
        cv.put(KEY_LONGITUDE, user.getLongitude());
        database.insert(DB_TABLE_USERS, null, cv);
    }

    public void insertUser(String username, double latitude, double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USERNAME, username);
        cv.put(KEY_LATITUDE, latitude);
        cv.put(KEY_LONGITUDE, longitude);
        database.insert(DB_TABLE_USERS, null, cv);
    }

    public void insertBook(Book book) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, book.getTitle());
        cv.put(KEY_AUTHOR, book.getAuthor());
        cv.put(KEY_ORIGINAL_OWNER, book.getOriginalOwner());
        cv.put(KEY_CURRENT_OWNER, book.getCurrentOwner());
        database.insert(DB_TABLE_BOOKS, null, cv);
    }

    public void updateUserLocation(String username, double latitude, double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_LATITUDE, latitude);
        cv.put(KEY_LONGITUDE, longitude);
        database.update(DB_TABLE_USERS, cv, KEY_USERNAME + "=\'" + username + "\'", null);
    }

    public void updateBookCurrentOwner(int id, String username) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_CURRENT_OWNER, username);
        database.update(DB_TABLE_BOOKS, cv, KEY_ID + "=\'" + id + "\'", null);
    }

    private void open() {
        database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DB_TABLE_USERS + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_USERNAME + " TEXT, "
                + KEY_LATITUDE + " REAL, "
                + KEY_LONGITUDE + " REAL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + DB_TABLE_BOOKS + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT, "
                + KEY_AUTHOR + " TEXT, "
                + KEY_ORIGINAL_OWNER + " TEXT, "
                + KEY_CURRENT_OWNER + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BOOKS);
        onCreate(db);
    }

    public void close() {
        database.close();
    }

    public Cursor getUserValues() {
        return this.database.query(DB_TABLE_USERS, new String[]{KEY_USERNAME, KEY_LATITUDE, KEY_LONGITUDE}, null, null, null, null, null);
    }

    public Cursor getBookValues() {
        return this.database.query(DB_TABLE_BOOKS, new String[]{KEY_ID, KEY_TITLE, KEY_AUTHOR, KEY_ORIGINAL_OWNER, KEY_CURRENT_OWNER}, null, null, null, null, null);
    }
}
