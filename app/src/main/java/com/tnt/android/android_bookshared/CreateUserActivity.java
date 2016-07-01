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
                    Toast toast = Toast.makeText(getApplicationContext(), "This user already exists in db!", Toast.LENGTH_LONG);
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

                    //write user to db
                    writeRecord(user);

                    //Toast info for creation user
                    Toast toast = Toast.makeText(getApplicationContext(), "This user save in db!", Toast.LENGTH_LONG);
                    toast.show();

                    //store current username in memory
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("user_details", MODE_PRIVATE);
                    sp.edit().putString("username", username).apply();

                    Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                    startActivity(intent);
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
