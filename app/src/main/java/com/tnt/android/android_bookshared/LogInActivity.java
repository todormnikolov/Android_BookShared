package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.tnt.android.android_bookshared.database.FirebaseDB;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;

public class LogInActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    Button btnLogIn;
    Button btnSignUp;
    CheckBox checkAutoLogin;

    private String inputUsername;
    private String inputPassword;
    boolean isLoginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Firebase.setAndroidContext(this);

        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnLogIn = (Button) findViewById(R.id.btn_log_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        checkAutoLogin = (CheckBox) findViewById(R.id.check_auto_login);

        btnLogIn.setOnClickListener(logInUserClick);
        btnSignUp.setOnClickListener(signUpUserClick);

        //deleteDatabase(UserDbHelper.DB_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
        boolean isLogged = sp.getBoolean("isLogged", false);
        boolean hasAutoLogin = sp.getBoolean("autoLogin", false);

        if (isNetworkConnected() && isLogged && hasAutoLogin) {
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    View.OnClickListener logInUserClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            inputUsername = editUsername.getText().toString().trim();
            inputPassword = editPassword.getText().toString().trim();

            if (!isNetworkConnected()) {
                Toast.makeText(getApplicationContext(), "For further using BOOKSHARED you must have a Internet connection. Connect to Internet and then try to log in again", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
                String currentUsername = sp.getString("username", "");

                if (!currentUsername.equals("")) {
                    if (inputUsername.equals(currentUsername)) {
                        String pass = sp.getString("password", "");

                        if (pass.equals(inputPassword)) {
                            sp.edit().putBoolean("isLogged", true).apply();
                            sp.edit().putBoolean("autoLogin", checkAutoLogin.isChecked()).apply();

                            isLoginSuccess = true;
                        }
                    } else {
                        Firebase ref = new Firebase("https://bookshared-9cc21.firebaseio.com/users");
                        ref.addValueEventListener(usernameExistsFirebase);
                    }
                }

                if (isLoginSuccess) {
                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong username or password! Try again", Toast.LENGTH_LONG).show();
                }
            }
        }

    };

    private void deleteSharedPreference(String name) {
        getSharedPreferences(name, 0).edit().clear().apply();
    }

    ValueEventListener usernameExistsFirebase = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot sn : dataSnapshot.getChildren()) {
                if (inputUsername.equals(sn.getKey())) {
                    for (DataSnapshot sn1 : sn.getChildren()) {
                        if (sn1.getKey().equals("password")) {
                            if (sn1.getValue().equals(inputPassword)) {
                                deleteSharedPreference(SharedPreferencesUtils.SP_USER);

                                SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
                                sp.edit().putString("username", inputUsername).apply();
                                sp.edit().putString("password", inputPassword).apply();
                                sp.edit().putBoolean("isLogged", true).apply();
                                sp.edit().putBoolean("autoLogin", checkAutoLogin.isChecked()).apply();

                                GeoFire geoFire = new GeoFire(new Firebase(FirebaseDB.DB_REF + ".child(" + FirebaseDB.USERS + ").child(" + inputUsername + ")"));
                                geoFire.getLocation(FirebaseDB.LOCATION, new LocationCallback() {
                                    @Override
                                    public void onLocationResult(String key, GeoLocation location) {
                                        if (location != null) {
                                            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
                                            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LATITUDE, (float) location.latitude).apply();
                                            sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LONGITUDE, (float) location.longitude).apply();
                                            isLoginSuccess = true;
                                        } else {
                                            isLoginSuccess = false;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        System.err.println("There was an error getting the GeoFire location: " + firebaseError);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };


    View.OnClickListener signUpUserClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LogInActivity.this, CreateUserActivity.class);
            startActivity(intent);
        }
    };

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
