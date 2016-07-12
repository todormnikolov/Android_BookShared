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

import com.tnt.android.android_bookshared.common.User;
import com.tnt.android.android_bookshared.database.FirebaseDB;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;
import com.tnt.android.android_bookshared.database.UserDbHelper;
import com.tnt.android.android_bookshared.database.UserDbUtils;

public class CreateUserActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    EditText editConfPassword;
    EditText editName;
    Button btnCreate;
    Button btnCancel;

    private UserDbUtils userDbUtils;

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

                    getUserLocation();

                    float latitude = sp.getFloat("latitude", 0f);
                    float longitude = sp.getFloat("longitude", 0f);

                    if ((latitude == 0f) || longitude == 0f) {
                        deleteSharedPreference(SharedPreferencesUtils.SP_USER);
                        Toast.makeText(getApplicationContext(), "Failed retrieve your location!", Toast.LENGTH_LONG).show();
                    } else {

                        user.setLatitude(latitude);
                        user.setLongitude(longitude);

                        Log.e("TAG", "lat: " + user.getLatitude() + ", long: " + user.getLongitude());

                        //save user to sqlite
                        userDbUtils.writeUserRecord(user);

                        //to delete
                        //printUserInSQLite();

                        // save user to firebase
                        FirebaseDB.writeUser(user);

                        Toast.makeText(getApplicationContext(), "Your profile is created! Username: " + username + " is ready to use", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(CreateUserActivity.this, LogInActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Impossible to create user with this input data!", Toast.LENGTH_LONG).show();
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

    private void printUserInSQLite() {
        Cursor cursor = userDbUtils.readUserRecord();

        if (cursor.moveToFirst()) {
            do {
                String nodeUsername = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_USERNAME));
                float latitude = cursor.getFloat(cursor.getColumnIndex(UserDbHelper.KEY_LATITUDE));
                float longitude = cursor.getFloat(cursor.getColumnIndex(UserDbHelper.KEY_LONGITUDE));
                Log.e("TAG", "Reading from database. User: " + nodeUsername + ", latitude: " + latitude + ", longitude: " + longitude);
//                    if (nodeUsername.equals(username)) {
//                        if (cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_)).equals(password)) {
//                            return true;
//                        }
//                    }

            } while (cursor.moveToNext());
        }

    }

    private void deleteSharedPreference(String name) {
        getSharedPreferences(name, 0).edit().clear().apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Bundle extras = data.getExtras();

            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            float longitude = (float)extras.getDouble("Longitude");
            float latitude = (float)extras.getDouble("Latitude");

            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LATITUDE, latitude).apply();
            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LONGITUDE, longitude).apply();

        }
    }

    private void getUserLocation() {
        Intent intent = new Intent(getApplicationContext(), GPSTrackerActivity.class);
        startActivityForResult(intent, 1);
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
