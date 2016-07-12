package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tnt.android.android_bookshared.common.Book;
import com.tnt.android.android_bookshared.common.Location;
import com.tnt.android.android_bookshared.database.FirebaseDB;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;
import com.tnt.android.android_bookshared.database.UserDbHelper;
import com.tnt.android.android_bookshared.database.UserDbUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    HashMap<String, Location> userLocations;
    ArrayList<Book> books;

    private UserDbUtils db;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        Firebase.setAndroidContext(this);

        db = UserDbUtils.getInstance(this);

        userLocations = new HashMap<>();
        books = new ArrayList<>();

        //initial book values
//        Book book1 = new Book("То","Стивън Кинг", "admin", "admin");
//        Book book2 = new Book("Той","Стивън Кинг", "admin", "admin");
//        Book book3 = new Book("Той","Стивън Хокинг", "admin", "admin");
//
//        db.writeBookRecord(book1);
//        db.writeBookRecord(book2);
//        db.writeBookRecord(book3);
//
//        books.add(book1);
//        books.add(book2);
//        books.add(book3);
//
//        FirebaseDB.saveBook(book1);
//        FirebaseDB.saveBook(book2);
//        FirebaseDB.saveBook(book3);

        ref = new Firebase(FirebaseDB.DB_REF).child(FirebaseDB.USERS);
        ref.addValueEventListener(getUsersFirebase);

        viewPager = (ViewPager) findViewById(R.id.books_pager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    //to delete
//    private void printUserLocation(HashMap<String, Location> userLocations) {
//
//        Set set = userLocations.entrySet();
//        Iterator iterator = set.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry entry = (Map.Entry) iterator.next();
//            String username = (String) entry.getKey();
//            Location location = (Location) entry.getValue();
//
//            Log.e("TAG", "Firebase user: Username: " + username + ", latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());
//        }
//
//    }

    //to delete
//    private void printUserBooks(ArrayList<Book> books) {
//        if (books != null && books.size() > 0) {
//            for (Book b : books) {
//                Log.e("TAG", "Book title: " + b.getTitle() + ", by " + b.getAuthor() + ", belongs to " + b.getOriginalOwner() + ", now is in " + b.getCurrentOwner());
//            }
//        } else {
//            Log.e("TAG", "Book array is empty");
//        }
//    }

    ValueEventListener getUsersFirebase = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot sn : dataSnapshot.getChildren()) {
                String username = String.valueOf(sn.getKey());

                for (DataSnapshot sn1 : sn.getChildren()) {
                    if (String.valueOf(sn1.getKey()).equals(FirebaseDB.BOOKS)) {
                        for (DataSnapshot sn2 : sn1.getChildren()) {
                            String title = String.valueOf(sn2.getKey());
                            String author = null;
                            String currentOwner = null;

                            for (DataSnapshot sn3 : sn2.getChildren()) {
                                if (String.valueOf(sn3.getKey()).equals(FirebaseDB.CHILD_AUTHOR)) {
                                    author = String.valueOf(sn3.getValue());
                                }

                                if (String.valueOf(sn3.getKey()).equals(FirebaseDB.CHILD_CURRENT_OWNER)) {
                                    currentOwner = String.valueOf(sn3.getValue());
                                }
                            }
                            books.add(new Book(title, author, username, currentOwner));
                        }
                    }

                    if (String.valueOf(sn1.getKey()).equals(FirebaseDB.LOCATION)) {
                        double latitude = 0;
                        double longitude = 0;
                        for (DataSnapshot sn2 : sn1.getChildren()) {
                            if (String.valueOf(sn2.getKey()).equals(FirebaseDB.CHILD_LATITUDE)) {
                                latitude = (double) sn2.getValue();
                            }
                            if (String.valueOf(sn2.getKey()).equals(FirebaseDB.CHILD_LONGITUDE)) {
                                longitude = (double) sn2.getValue();
                            }
                        }
                        userLocations.put(username, new Location(latitude, longitude));
                    }
                }
            }

            //to delete
            //printUserLocation(userLocations);
            //printUserBooks(books);
            syncUsersData();
            readSQLiteUsers();

            if (books != null && books.size() > 0) {
                syncBooksData();

                //to delete - read books from sqlite
                readSQLiteBooks();
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };

    //to delete
    private void readSQLiteUsers() {
        Cursor cursor = db.readUserRecord();
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_USERNAME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LONGITUDE));

                Log.e("TAG", "User " + ": " + username + " with location: latitude = " + latitude + " and longitude = " + longitude);
            } while (cursor.moveToNext());
        } else {
            Log.e("TAG", "No users in SQLite");
        }
    }


    //to delete
    private void readSQLiteBooks() {
        Cursor cursor = db.readBookRecord();
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_TITLE));
                String author = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_AUTHOR));
                String originalOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_ORIGINAL_OWNER));
                String currentOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_ORIGINAL_OWNER));

                Log.e("TAG", "Book " + ": " + title + ", by " + author + ", owner: " + originalOwner + ", current owner: " + currentOwner);
            } while (cursor.moveToNext());
        } else {
            Log.e("TAG", "No books in SQLite");
        }
    }

    private boolean bookRowExists(Book book) {
        Cursor cursor = db.readBookRecord();
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_TITLE));
                String author = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_AUTHOR));
                String originalOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_ORIGINAL_OWNER));
                String currentOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_CURRENT_OWNER));

//                if (book.getTitle().equals(title) && book.getAuthor().equals(author)
//                        && book.getOriginalOwner().equals(originalOwner) && book.getCurrentOwner().equals(currentOwner)) {
//                    return true;
//                }

                if (book.getTitle().equals(title) && book.getAuthor().equals(author)
                        && book.getOriginalOwner().equals(originalOwner)) {
                    return true;
                }
            } while (cursor.moveToNext());
        }

        return false;
    }

    private void syncBooksData() {
        Cursor cursor = db.readBookRecord();
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_TITLE));
                String author = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_AUTHOR));
                String originalOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_ORIGINAL_OWNER));
                //String currentOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_CURRENT_OWNER));

                for (Book book : books) {
                    if ((book.getTitle()).equals(title) && (book.getAuthor()).equals(author) && (book.getOriginalOwner()).equals(originalOwner)) {
//                        Log.e("TAG", "book.getTitle() = " + book.getTitle());
//                        Log.e("TAG", "book.getAuthor() = " + book.getAuthor());
//                        Log.e("TAG", "book.getOriginalOwner() = " + book.getOriginalOwner());
//                        Log.e("TAG", "book.getCurrentOwner() = " + book.getCurrentOwner());

//                        if (!((book.getCurrentOwner()).equals(currentOwner))) {
//                            db.updateBook(cursor.getColumnIndex(UserDbHelper.KEY_ID), book.getCurrentOwner());
//                        } else {
                        break;
                        //}
                    }
                }

            } while (cursor.moveToNext());
        }

        if (books.size() > 0)

        {
            for (Book book : books) {

                if (!bookRowExists(book)) {
                    db.writeBookRecord(book);
                }
            }
        }
    }

    private boolean userRowExists(String username, double latitude, double longitude) {
        Cursor cursor = db.readUserRecord();
        if (cursor.moveToFirst()) {
            do {
                String usernameDb = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_USERNAME));
                double latitudeDb = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LATITUDE));
                double longitudeDb = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LONGITUDE));

                if (username.equals(usernameDb) && latitude == latitudeDb && longitude == longitudeDb) {
                    return true;
                }
            } while (cursor.moveToNext());
        }

        return false;
    }

    private void syncUsersData() {
        Cursor cursor = db.readUserRecord();
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_USERNAME));

                Location location = userLocations.get(username);
                if (location != null) {
                    double latitude = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LONGITUDE));

                    if (location.getLatitude() != latitude || location.getLongitude() != longitude) {
                        db.updateUserLocation(username, location.getLatitude(), location.getLongitude());
                        userLocations.remove(username);
                    }
                }

            } while (cursor.moveToNext());
        }

        Set set = userLocations.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String username = (String) entry.getKey();
            Location location = (Location) entry.getValue();

            if (!userRowExists(username, location.getLatitude(), location.getLongitude())) {
                db.writeUserRecord(username, location.getLatitude(), location.getLongitude());
            }
        }
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);

            int tabsCount = vg.getChildCount();
            for (int i = 0; i < tabsCount; i++) {
                int delay = (i * 150) + 250;
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(i);
                vgTab.setScaleX(0f);
                vgTab.setScaleY(0f);

                vgTab.animate().scaleX(1f).scaleY(1f).setStartDelay(delay)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .setDuration(450)
                        .start();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AllBooksFragment(), "ALL BOOKS");
        adapter.addFragment(new UserBooksFragment(), "MY BOOKS");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
        boolean isLogged = sp.getBoolean(SharedPreferencesUtils.SP_IS_LOGGED, false);

        if (!isLogged) {
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.user_edit_location) {

            Intent intent = new Intent(getApplicationContext(), EditLocationActivity.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.book_search) {

            Toast.makeText(getApplicationContext(), "This function don\'t work yet", Toast.LENGTH_SHORT).show();

            return true;

        } else if (id == R.id.user_log_out) {
            SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
            sp.edit().putBoolean(SharedPreferencesUtils.SP_IS_LOGGED, false).apply();

            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
        } else if (id == android.R.id.home) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Bundle extras = data.getExtras();
            double latitude = extras.getDouble("Latitude");
            double longitude = extras.getDouble("Longitude");

            SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LATITUDE, (float) latitude).apply();
            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LONGITUDE, (float) longitude).apply();

            FirebaseDB.saveLocation(sp.getString(SharedPreferencesUtils.SP_USERNAME, ""), (float) latitude, (float) longitude);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
