package com.tnt.android.android_bookshared;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tnt.android.android_bookshared.common.Book;

import java.util.ArrayList;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserBooksHolder> {

    private ArrayList<Book> books;

    public UserAdapter(Context context, ArrayList<Book> books) {
        this.books = books;
    }

    @Override
    public UserBooksHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_book_holder, parent, false);
        UserBooksHolder holder = new UserBooksHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserBooksHolder holder, int position) {
        final Book book = books.get(position);
        holder.txtUserBookTitle.setText(book.getTitle());
        holder.txtUserBookAuthor.setText(book.getAuthor());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class UserBooksHolder extends RecyclerView.ViewHolder {

        TextView txtUserBookTitle;
        TextView txtUserBookAuthor;

        public UserBooksHolder(View itemView) {
            super(itemView);

            txtUserBookTitle = (TextView) itemView.findViewById(R.id.txt_user_book_title);
            txtUserBookAuthor = (TextView) itemView.findViewById(R.id.txt_user_book_author);
        }
    }
}
