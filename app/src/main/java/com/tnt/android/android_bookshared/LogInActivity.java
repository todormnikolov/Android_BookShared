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

import com.firebase.client.Firebase;

public class LogInActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    Button btnLogIn;
    Button btnSignUp;
    CheckBox checkAutoLogin;

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

            final String inputUsername = editUsername.getText().toString().trim();
            final String inputPassword = editPassword.getText().toString().trim();

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

                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }

                Toast.makeText(getApplicationContext(), "Incorrect input data! Try again", Toast.LENGTH_LONG).show();
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
