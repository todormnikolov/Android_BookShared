package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tnt.android.android_bookshared.database.UserDbHelper;
import com.tnt.android.android_bookshared.database.UserDbUtils;

public class LogInActivity extends AppCompatActivity {

    EditText editUsername;
    EditText editPassword;
    Button btnSignIn;
    Button btnSignUp;

    UserDbUtils userDbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);

        userDbUtils = UserDbUtils.getInstance(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputUsername = editUsername.getText().toString();
                String inputPassword = editPassword.getText().toString();

                //check for correct username and password
                if (userExists(inputUsername, inputPassword)) {

                    SharedPreferences sp = getApplicationContext().getSharedPreferences("user_details", MODE_PRIVATE);
                    sp.edit().putString("username", inputUsername).apply();

                    Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry access denied, try with correct username and/or password to log in!", Toast.LENGTH_LONG).show();
                }

            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, CreateUserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getApplicationContext().getSharedPreferences("user_details", MODE_PRIVATE);
        String currentUsername = sp.getString("username", "");

        //check for log in user and redirect to main activity
        if (!currentUsername.equals("")) {
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean userExists(String username, String password) {
        Cursor cursor = userDbUtils.readUserRecord();

        if (cursor.moveToFirst()) {
            do {
                String nodeUsername = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_USERNAME));

                if (nodeUsername.equals(username)) {
                    if (cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_PASSWORD)).equals(password)) {
                        return true;
                    }
                }

            } while (cursor.moveToNext());
        }
        return false;
    }
}
