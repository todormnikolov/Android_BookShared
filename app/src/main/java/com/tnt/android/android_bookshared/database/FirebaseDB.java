package com.tnt.android.android_bookshared.database;

import com.firebase.client.Firebase;
import com.tnt.android.android_bookshared.common.Book;
import com.tnt.android.android_bookshared.common.User;


public class FirebaseDB {

    public static final String DB_REF = "https://bookshared-9cc21.firebaseio.com/";
    public static final String USERS = "users";
    public static final String BOOKS = "books";
    public static final String LOCATION = "location";

    public static final String CHILD_PASSWORD = "password";
    public static final String CHILD_NAME = "name";
    public static final String CHILD_LATITUDE = "latitude";
    public static final String CHILD_LONGITUDE = "longitude";
    public static final String CHILD_AUTHOR = "author";
    public static final String CHILD_CURRENT_OWNER = "currentOwner";

    public static void writeUser(User user) {
        String username = user.getUsername();
        Firebase ref = new Firebase(DB_REF).child(USERS);
        ref.child(username).child(CHILD_PASSWORD).setValue(user.getPassword());
        ref.child(username).child(CHILD_NAME).setValue(user.getName());
        ref.child(username).child(LOCATION).child(CHILD_LATITUDE).setValue(user.getLatitude());
        ref.child(username).child(LOCATION).child(CHILD_LONGITUDE).setValue(user.getLongitude());
    }

    public static void saveLocation(String username, float latitude, float longitude) {
        Firebase ref = new Firebase(DB_REF).child(USERS).child(username).child(LOCATION);
        ref.child(CHILD_LATITUDE).setValue(latitude);
        ref.child(CHILD_LONGITUDE).setValue(longitude);
    }

    public static void saveBook(Book book) {
        String title = book.getTitle();
        String owner = book.getOriginalOwner();
        Firebase ref = new Firebase(DB_REF).child(USERS).child(owner).child(BOOKS).child(title);

        ref.child(CHILD_AUTHOR).setValue(book.getAuthor());
        ref.child(CHILD_CURRENT_OWNER).setValue(book.getCurrentOwner());
    }
}
