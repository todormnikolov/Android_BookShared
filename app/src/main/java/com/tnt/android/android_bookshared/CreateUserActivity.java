package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.tnt.android.android_bookshared.common.User;
import com.tnt.android.android_bookshared.database.UserDbHelper;
import com.tnt.android.android_bookshared.database.UserDbUtils;

public class CreateUserActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    EditText editConfPassword;
    EditText editName;
    Button btnCreate;
    Button btnCancel;

    UserDbUtils userDbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_account);
        userDbUtils = UserDbUtils.getInstance(this);

        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
        editConfPassword = (EditText) findViewById(R.id.edit_conf_password);
        editName = (EditText) findViewById(R.id.edit_name);
        btnCreate = (Button) findViewById(R.id.btn_create);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get value from username field
                String username = editUsername.getText().toString().trim();

                //Check for username exists in db
                if (usernameExists(username)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "This username is not available!", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                //get values from pass and re-pass fields
                String password = editPassword.getText().toString().trim();
                String rePassword = editConfPassword.getText().toString().trim();

                //compare two fields for equals password
                if (password.length() > 2 && password.equals(rePassword)) {

                    String name = editName.getText().toString().trim();

                    //check for valid name

                    //escape strings

                    User user = new User(username, password, name);

                    //store data for current username in memory
                    SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
                    sp.edit().putString("username", user.getUsername()).apply();
                    sp.edit().putString("password", user.getPassword()).apply();

                    //write user to db
                    //writeRecord(user);

                    // save to firebase
                    writeUserToFirebase(user);
                    getUserLocation();

                    if ((sp.getFloat("latitude", 0f) == 0f) || sp.getFloat("longitude", 0f) == 0f){
                        Toast toast = Toast.makeText(getApplicationContext(), "Failed retrieve your location!", Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "Your profile is created! Username: " + username + " is ready to use", Toast.LENGTH_LONG);
                        toast.show();

                        Intent intent = new Intent(CreateUserActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Bundle extras = data.getExtras();

            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            double longitude = extras.getDouble("Longitude");
            double latitude = extras.getDouble("Latitude");

            sp.edit().putFloat("longitude", (float) longitude).apply();
            sp.edit().putFloat("latitude", (float) latitude).apply();

            saveLocationToFirebase(sp.getString("username", ""), latitude, longitude);
        }
    }

    public static void saveLocationToFirebase(String username, double latitude, double longitude) {
        GeoFire geoFire = new GeoFire(new Firebase("https://bookshared-9cc21.firebaseio.com/users/" + username));
        geoFire.setLocation("location", new GeoLocation(latitude, longitude));
    }

    private void getUserLocation() {
        Intent intent = new Intent(getApplicationContext(), GPSTrackerActivity.class);
        startActivityForResult(intent, 1);
    }

    private void writeUserToFirebase(User user) {
        Firebase ref = new Firebase("https://bookshared-9cc21.firebaseio.com/");
        Firebase userRef = ref.child("users").child(user.getUsername());
        userRef.setValue(user);
    }

    //not using
    private void writeRecord(User user) {
        userDbUtils.writeUserRecord(user);
    }

    private boolean usernameExists(String username) {
        Cursor cursor = userDbUtils.readUserRecord();
        if (cursor.moveToFirst()) {
            do {
                String nodeUsername = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_USERNAME));

                if (nodeUsername.equals(username)) {
                    return true;
                }

            } while (cursor.moveToNext());
        }
        return false;
    }
}
