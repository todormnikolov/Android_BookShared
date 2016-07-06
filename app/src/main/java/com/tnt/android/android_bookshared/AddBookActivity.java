package com.tnt.android.android_bookshared;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddBookActivity extends AppCompatActivity {

    EditText editBookTitle;
    EditText editBookAuthor;
    Button btnAddBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        editBookTitle = (EditText) findViewById(R.id.edit_book_title);
        editBookAuthor = (EditText) findViewById(R.id.edit_book_author);
        btnAddBook = (Button) findViewById(R.id.btn_add_book);

        btnAddBook.setOnClickListener(addBookClick);

    }

    View.OnClickListener addBookClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String title = editBookTitle.getText().toString().trim();
            String author = editBookAuthor.getText().toString().trim();

            //Validation input strings

            SharedPreferences sp = getSharedPreferences("user_details", MODE_PRIVATE);
            String username = sp.getString("username", "");

            //save to sqlite

            Log.d("TAG", "Title book: " + title);
            Log.d("TAG", "Book author: " + author);

            finish();
        }
    };
}
