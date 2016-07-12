package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tnt.android.android_bookshared.database.FirebaseDB;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;
import com.tnt.android.android_bookshared.database.UserDbUtils;

public class EditLocationActivity extends AppCompatActivity {

    TextView txtLatitudeValue;
    TextView txtLongitudeValue;
    Button btnViewLocation;
    Button btnGetCurrentLocation;

    UserDbUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        txtLatitudeValue = (TextView) findViewById(R.id.txt_latitude_value);
        txtLongitudeValue = (TextView) findViewById(R.id.txt_longitude_value);
        btnViewLocation = (Button) findViewById(R.id.btn_view_location);
        btnGetCurrentLocation = (Button) findViewById(R.id.btn_get_current_location);

        btnViewLocation.setOnClickListener(viewLocationOnMap);
        btnGetCurrentLocation.setOnClickListener(getUserLocation);

        db = UserDbUtils.getInstance(this);

        SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
        txtLatitudeValue.setText(String.valueOf(sp.getFloat(SharedPreferencesUtils.SP_USER_LATITUDE, 0f)));
        txtLongitudeValue.setText(String.valueOf(sp.getFloat(SharedPreferencesUtils.SP_USER_LONGITUDE, 0f)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Bundle extras = data.getExtras();

            SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
            String username = sp.getString(SharedPreferencesUtils.SP_USERNAME, "");

            float latitude = (float) extras.getDouble("Latitude");
            float longitude = (float) extras.getDouble("Longitude");

            //save location to SharedPreferences
            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LATITUDE, latitude).apply();
            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LONGITUDE, longitude).apply();

            txtLatitudeValue.setText(String.valueOf(latitude));
            txtLongitudeValue.setText(String.valueOf(longitude));

            //save new location in sqlite
            db.updateUserLocation(username, latitude, longitude);

            //save new location in Firebase
            FirebaseDB.saveLocation(username, latitude, longitude);

            Toast.makeText(getApplicationContext(), "Successfully taken your present location", Toast.LENGTH_LONG).show();
        }
    }

    View.OnClickListener viewLocationOnMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
            float latitude = sp.getFloat(SharedPreferencesUtils.SP_USER_LATITUDE, 0f);
            float longitude = sp.getFloat(SharedPreferencesUtils.SP_USER_LONGITUDE, 0f);

            if (latitude == 0f || longitude == 0f) {
                Toast.makeText(getApplicationContext(), "Your location is unknown!", Toast.LENGTH_LONG).show();
            } else {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }
        }
    };

    View.OnClickListener getUserLocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
            String username = sp.getString(SharedPreferencesUtils.SP_USERNAME, "");

            if (!username.equals("")) {
                Intent intent = new Intent(getApplicationContext(), GPSTrackerActivity.class);
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(getApplicationContext(), "Not active user", Toast.LENGTH_LONG).show();
            }
        }
    };
}
