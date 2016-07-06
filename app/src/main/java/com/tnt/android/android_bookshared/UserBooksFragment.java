package com.tnt.android.android_bookshared;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by USER on 2.7.2016 г..
 */
public class UserBooksFragment extends Fragment implements View.OnClickListener {

    View view;
    Button btnAddBook;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.user_books, container, false);

        btnAddBook = (Button) view.findViewById(R.id.btn_new_book);
        btnAddBook.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_new_book) {

            Intent intent = new Intent(getActivity(), AddBookActivity.class);
            startActivity(intent);
        }
    }
}
