package com.tnt.android.android_bookshared;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tnt.android.android_bookshared.common.Book;
import com.tnt.android.android_bookshared.database.FirebaseDB;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;
import com.tnt.android.android_bookshared.database.UserDbHelper;
import com.tnt.android.android_bookshared.database.UserDbUtils;

public class AddBookActivity extends AppCompatActivity {

    EditText editBookTitle;
    EditText editBookAuthor;
    Button btnAddBook;
    UserDbUtils db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        editBookTitle = (EditText) findViewById(R.id.edit_book_title);
        editBookAuthor = (EditText) findViewById(R.id.edit_book_author);
        btnAddBook = (Button) findViewById(R.id.btn_add_book);
        btnAddBook.setOnClickListener(addBookClick);

        db = UserDbUtils.getInstance(this);
    }

    View.OnClickListener addBookClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String title = editBookTitle.getText().toString().trim();
            String author = editBookAuthor.getText().toString().trim();

            //Validation input strings

            SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.SP_USER, MODE_PRIVATE);
            String username = sp.getString(SharedPreferencesUtils.SP_USERNAME, "");

            Book book = new Book(title, author, username, username);

            //save to sqlite
            db.writeBookRecord(book);

            //printBookLogFromSQLite();

            //save to Firebase
            FirebaseDB.saveBook(book);

            finish();
        }
    };

    private void printBookLogFromSQLite() {
        Cursor cursor = db.readBookRecord();

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_TITLE));
                String author = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_AUTHOR));
                String originalOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_ORIGINAL_OWNER));
                String currentOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_CURRENT_OWNER));
            } while (cursor.moveToNext());
        }

    }
}
