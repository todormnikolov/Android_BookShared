//package com.tnt.android.android_bookshared.database;
//
//import android.content.Context;
//import android.database.Cursor;
//
//import com.tnt.android.android_bookshared.common.Book;
//
//
//public class BookDbUtils {
//
//    private static BookDbUtils instance;
//    private BookDbHelper db;
//
//    private BookDbUtils(Context context) {
//        initDB(context);
//    }
//
//    public static BookDbUtils getInstance(Context context) {
//        if (instance == null) {
//            instance = new BookDbUtils(context);
//        }
//        return instance;
//    }
//
//    public void writeBookRecord(Book book) {
//        db.insertBook(book);
//    }
//
//    public void updateBook(int id, String username){
//        db.updateBookCurrentOwner(id, username);
//    }
//
//    public Cursor readBookRecord() {
//        return db.getBookValues();
//    }
//
//    private BookDbHelper initDB(Context context) {
//        if (db == null) {
//            db = new BookDbHelper(context);
//        }
//        return db;
//    }
//}
