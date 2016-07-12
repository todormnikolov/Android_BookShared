package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.tnt.android.android_bookshared.database.FirebaseDB;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;
import com.tnt.android.android_bookshared.database.UserDbHelper;

public class LogInActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    Button btnLogIn;
    Button btnSignUp;
    CheckBox checkAutoLogin;

    private String inputUsername;
    private String inputPassword;

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
        //deleteSharedPreference(SharedPreferencesUtils.SP_USER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
        boolean isLogged = sp.getBoolean(SharedPreferencesUtils.SP_IS_LOGGED, false);
        boolean hasAutoLogin = sp.getBoolean(SharedPreferencesUtils.SP_AUTO_LOGIN, false);

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
                SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
                String currentUsername = sp.getString(SharedPreferencesUtils.SP_USERNAME, "");

                if (!currentUsername.equals("")) {
                    if (inputUsername.equals(currentUsername)) {
                        String pass = sp.getString(SharedPreferencesUtils.SP_PASSWORD, "");

                        if (pass.equals(inputPassword)) {
                            sp.edit().putBoolean(SharedPreferencesUtils.SP_IS_LOGGED, true).apply();
                            sp.edit().putBoolean(SharedPreferencesUtils.SP_AUTO_LOGIN, checkAutoLogin.isChecked()).apply();

                            createIntent();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong password! Try again", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Firebase ref = new Firebase(FirebaseDB.DB_REF).child(FirebaseDB.USERS);
                        ref.addValueEventListener(usernameExistsFirebase);
                    }
                }
            }
        }

    };

    private void createIntent() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void deleteSharedPreference(String name) {
        getSharedPreferences(name, 0).edit().clear().apply();
    }

    ValueEventListener usernameExistsFirebase = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            boolean isFoundUser = false;
            boolean isMatchPassword = false;
            for (DataSnapshot sn : dataSnapshot.getChildren()) {
                if (inputUsername.equals(sn.getKey())) {
                    isFoundUser = true;
                    for (DataSnapshot sn1 : sn.getChildren()) {
                        if (sn1.getKey().equals(FirebaseDB.CHILD_PASSWORD)) {
                            if (sn1.getValue().equals(inputPassword)) {
                                isMatchPassword = true;
                                deleteSharedPreference(SharedPreferencesUtils.SP_USER);

                                SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
                                sp.edit().putString(SharedPreferencesUtils.SP_USERNAME, inputUsername).apply();
                                sp.edit().putString(SharedPreferencesUtils.SP_PASSWORD, inputPassword).apply();
                                sp.edit().putBoolean(SharedPreferencesUtils.SP_IS_LOGGED, true).apply();
                                sp.edit().putBoolean(SharedPreferencesUtils.SP_AUTO_LOGIN, checkAutoLogin.isChecked()).apply();

                            } else {
                                Toast.makeText(getApplicationContext(), "Wrong password! Try again", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                        if (isMatchPassword && String.valueOf(sn1.getKey()).equals(FirebaseDB.LOCATION)) {
                            for (DataSnapshot sn2 : sn1.getChildren()) {
                                SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);

                                if (String.valueOf(sn2.getKey()).equals(FirebaseDB.CHILD_LATITUDE)) {
                                    sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LATITUDE, (float) sn2.getValue()).apply();
                                }
                                if (String.valueOf(sn2.getKey()).equals(FirebaseDB.CHILD_LONGITUDE)) {
                                    sp.edit().putFloat(SharedPreferencesUtils.SP_USER_LONGITUDE, (float) sn2.getValue()).apply();
                                }
                            }
                        }
                    }
                }
            }
            if (!isFoundUser) {
                Toast.makeText(getApplicationContext(), "Wrong username! Try again", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            Log.e("TAG", "There was an error getting the Firebase data: " + firebaseError);
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
