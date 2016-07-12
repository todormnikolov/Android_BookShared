package com.tnt.android.android_bookshared.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tnt.android.android_bookshared.common.Book;

//public class BookDbHelper extends SQLiteOpenHelper {
//
//    protected SQLiteDatabase database;
//
//    public static final String DB_TABLE_BOOKS = "DB_TABLE_BOOKS";
//    public static final String KEY_ID = "_id";
//    public static final String KEY_TITLE = "TITLE";
//    public static final String KEY_AUTHOR = "AUTHOR";
//    public static final String KEY_ORIGINAL_OWNER = "ORIGINAL_OWNER";
//    public static final String KEY_CURRENT_OWNER = "CURRENT_OWNER";
//
//    public BookDbHelper(Context context) {
//        super(context, UserDbHelper.DB_NAME, null, UserDbHelper.DB_VERSION);
//        open();
//    }
//
//    public void insertBook(Book book) {
//        ContentValues cv = new ContentValues();
//        cv.put(KEY_TITLE, book.getTitle());
//        cv.put(KEY_AUTHOR, book.getAuthor());
//        cv.put(KEY_ORIGINAL_OWNER, book.getOriginalOwner());
//        cv.put(KEY_CURRENT_OWNER, book.getCurrentOwner());
//        database.insert(DB_TABLE_BOOKS, null, cv);
//    }
//
//    public void updateBookCurrentOwner(int id, String username) {
//        ContentValues cv = new ContentValues();
//        cv.put(KEY_CURRENT_OWNER, username);
//        database.update(DB_TABLE_BOOKS, cv, KEY_ID + "=\'" + id + "\'", null);
//    }
//
//    private void open() {
//        database = this.getWritableDatabase();
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE IF NOT EXISTS "
//                + DB_TABLE_BOOKS + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + KEY_TITLE + " TEXT, "
//                + KEY_AUTHOR + " TEXT, "
//                + KEY_ORIGINAL_OWNER + " TEXT, "
//                + KEY_CURRENT_OWNER + " TEXT);");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BOOKS);
//        onCreate(db);
//    }
//
//    public void close() {
//        database.close();
//    }
//
//    public Cursor getBookValues() {
//        return this.database.query(DB_TABLE_BOOKS, new String[]{KEY_ID, KEY_TITLE, KEY_AUTHOR, KEY_ORIGINAL_OWNER, KEY_CURRENT_OWNER}, null, null, null, null, null);
//    }
//}
//