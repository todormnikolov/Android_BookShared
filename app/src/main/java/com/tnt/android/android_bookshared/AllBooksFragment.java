package com.tnt.android.android_bookshared;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tnt.android.android_bookshared.common.Book;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;
import com.tnt.android.android_bookshared.database.UserDbHelper;
import com.tnt.android.android_bookshared.database.UserDbUtils;

import java.util.ArrayList;


public class AllBooksFragment extends Fragment {

    View view;
    RecyclerView recViewBook;

    ArrayList<Book> books;
    UserDbUtils db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        db = UserDbUtils.getInstance(getActivity());

        view = inflater.inflate(R.layout.all_books, container, false);
        recViewBook = (RecyclerView) view.findViewById(R.id.rec_view_all_books);
        recViewBook.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private ArrayList<Book> getUserBooks() {
        ArrayList<Book> books = new ArrayList<>();

        SharedPreferences sp = getActivity().getSharedPreferences(SharedPreferencesUtils.SP_USER, Context.MODE_PRIVATE);
        String currentUser = sp.getString(SharedPreferencesUtils.SP_USERNAME, "");
        if (currentUser.equals("")) {
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent);
        } else {
            Cursor cursor = db.readBookRecord();

            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_TITLE));
                    String author = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_AUTHOR));
                    String originalOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_ORIGINAL_OWNER));
                    String currentOwner = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_CURRENT_OWNER));

                    if (!originalOwner.equals(currentUser)) {
                        books.add(new Book(title, author, originalOwner, currentOwner));
                    }

                } while (cursor.moveToNext());
            }
        }
        return books;
    }

    @Override
    public void onResume() {
        super.onResume();
        books = getUserBooks();
        recViewBook.setAdapter(new AllBooksAdapter(getActivity(), books));
        recViewBook.getAdapter().notifyDataSetChanged();
    }
}
