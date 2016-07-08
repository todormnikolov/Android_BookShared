package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tnt.android.android_bookshared.database.FirebaseDB;

public class EditLocationActivity extends AppCompatActivity {

    TextView txtLatitudeValue;
    TextView txtLongitudeValue;
    Button btnViewLocation;
    Button btnGetCurrentLocation;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
        txtLatitudeValue.setText(String.valueOf(sp.getFloat("latitude", 0f)));
        txtLongitudeValue.setText(String.valueOf(sp.getFloat("longitude", 0f)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Bundle extras = data.getExtras();
            double longitude = extras.getDouble("Longitude");
            double latitude = extras.getDouble("Latitude");

            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);

            sp.edit().putFloat("longitude", (float) longitude).apply();
            sp.edit().putFloat("latitude", (float) latitude).apply();
        }
    }

    View.OnClickListener viewLocationOnMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            float latitude = sp.getFloat("latitude", 0f);
            float longitude = sp.getFloat("longitude", 0f);

            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);

            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            startActivity(mapIntent);
        }
    };

    View.OnClickListener getUserLocation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), GPSTrackerActivity.class);
            startActivityForResult(intent, 1);

            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            FirebaseDB.saveLocationToFirebase(sp.getString("username", ""), sp.getFloat("latitude", 0f), sp.getFloat("longitude", 0f));

            Toast.makeText(getApplicationContext(), "Your longitude: " + sp.getFloat("latitude", 0f) + " and your latitude: " + sp.getFloat("longitude", 0f), Toast.LENGTH_LONG).show();
        }
    };
}
