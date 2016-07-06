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
import com.tnt.android.android_bookshared.common.User;

public class LogInActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    Button btnLogIn;
    Button btnSignUp;
    CheckBox checkAutoLogin;

    boolean isLocationChecked;

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
    }

    private boolean isSetUserLocation() {

        SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
        float longitude = sp.getFloat("longitude", 0f);
        float latitude = sp.getFloat("latitude", 0f);

        return (longitude == 0f || latitude == 0f) ? false : true;
    }

    private void getUserLocation() {
        Intent intent = new Intent(getApplicationContext(), GPSTrackerActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLocationChecked && !isSetUserLocation()) {
            getUserLocation();
        } else {
            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            boolean isLogged = sp.getBoolean("isLogged", false);

            if (isNetworkConnected() && isLogged) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        isLocationChecked = true;

        if (requestCode == 1) {
            Bundle extras = data.getExtras();

            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            sp.edit().putFloat("longitude", (float) extras.getDouble("Longitude")).apply();
            sp.edit().putFloat("latitude", (float) extras.getDouble("Latitude")).apply();

        }
    }

    View.OnClickListener logInUserClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String inputUsername = editUsername.getText().toString().trim();
            final String inputPassword = editPassword.getText().toString().trim();

            if (!isNetworkConnected()) {
                Toast.makeText(getApplicationContext(), "For further using BOOKSHARED you must have a Internet connection. Connect to Internet and then try to log in again", Toast.LENGTH_LONG).show();
            } else if (!isSetUserLocation()) {
                Toast.makeText(getApplicationContext(), "Your location is unknown!", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
                String currentUsername = sp.getString("username", "");

                if (!currentUsername.equals("")) {
                    if (inputUsername.equals(currentUsername)) {
                        String pass = sp.getString("password", "");

                        if (pass.equals(inputPassword)) {
                            sp.edit().putBoolean("isLogged", checkAutoLogin.isChecked()).apply();

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    Firebase ref = new Firebase("https://bookshared-9cc21.firebaseio.com/users");

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            User user;

                            for (DataSnapshot sn : snapshot.getChildren()) {
                                user = sn.getValue(User.class);
                                if (user.getUsername().equals(inputUsername)) {
                                    if (user.getPassword().equals(inputPassword)) {

                                        SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
                                        sp.edit().putString("username", user.getUsername()).apply();
                                        sp.edit().putString("password", user.getPassword()).apply();
                                        sp.edit().putBoolean("isLogged", checkAutoLogin.isChecked()).apply();

                                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.out.println("The read failed: " + firebaseError.getMessage());
                        }
                    });
                }
            }
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
