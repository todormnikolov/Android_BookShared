package com.tnt.android.android_bookshared.database;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.tnt.android.android_bookshared.common.User;


public class FirebaseDB {

    public static String DB_REF = "https://bookshared-9cc21.firebaseio.com/";
    public static String USERS = "users";
    public static String LOCATION = "location";

    public static String CHILD_PASSWORD = "password";
    public static String CHILD_NAME = "name";

    public static void writeUserToFirebase(User user) {
        String username = user.getUsername();
        Firebase ref = new Firebase(DB_REF).child(USERS).child(username);
        ref.child(CHILD_PASSWORD).setValue(user.getPassword());
        ref.child(CHILD_NAME).setValue(user.getName());
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(LOCATION, new GeoLocation(user.getLatitude(), user.getLongitude()));
    }

    public static void saveLocationToFirebase(String username, float latitude, float longitude) {
        GeoFire geoFire = new GeoFire(new Firebase(DB_REF).child(USERS).child(username));
        geoFire.setLocation(LOCATION, new GeoLocation(latitude, longitude));
    }
}
