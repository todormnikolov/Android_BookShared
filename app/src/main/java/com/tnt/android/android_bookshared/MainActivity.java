package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.tnt.android.android_bookshared.database.FirebaseDB;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.books_pager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(onPageChangeListener);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

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
        public void onPageScrollStateChanged(int state) {}
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

        SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
        boolean isLogged = sp.getBoolean("isLogged", false);

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

            //Book search is clicked

            return true;

        } else if (id == R.id.user_log_out) {
            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            sp.edit().putBoolean("isLogged", false).apply();

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
            double longitude = extras.getDouble("Longitude");
            double latitude = extras.getDouble("Latitude");

            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);

            sp.edit().putFloat("longitude", (float) longitude).apply();
            sp.edit().putFloat("latitude", (float) latitude).apply();

            FirebaseDB.saveLocationToFirebase(sp.getString("username", ""), (float) latitude, (float) longitude);
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
