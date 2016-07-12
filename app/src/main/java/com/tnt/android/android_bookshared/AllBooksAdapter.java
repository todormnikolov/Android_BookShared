package com.tnt.android.android_bookshared;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tnt.android.android_bookshared.common.Book;
import com.tnt.android.android_bookshared.common.Location;
import com.tnt.android.android_bookshared.database.SharedPreferencesUtils;
import com.tnt.android.android_bookshared.database.UserDbHelper;
import com.tnt.android.android_bookshared.database.UserDbUtils;

import java.util.ArrayList;


public class AllBooksAdapter extends RecyclerView.Adapter<AllBooksAdapter.AllBooksHolder> {

    private ArrayList<Book> books;
    Context context;
    private UserDbUtils db;

    public AllBooksAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
        db = UserDbUtils.getInstance(context);
    }

    @Override
    public AllBooksHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_books_holder, parent, false);
        AllBooksHolder holder = new AllBooksHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(AllBooksHolder holder, int position) {
        final Book book = books.get(position);
        holder.txtBookTitleHolder.setText(book.getTitle());
        holder.txtBookAuthorHolder.setText(book.getAuthor());
        holder.txtUserDistance.setText(String.valueOf(calculateDistance(book.getOriginalOwner())));
    }

    private double calculateDistance(String originalOwner) {
        SharedPreferences sp = this.context.getSharedPreferences(SharedPreferencesUtils.SP_USER, Context.MODE_PRIVATE);
        String username = sp.getString(SharedPreferencesUtils.SP_USERNAME, "");
        if (!username.equals("")) {
            Location ownerLocation = getLocation(originalOwner);
            Location currentUserLocation = getLocation(username);
            float[] distanceResults = new float[1];
            android.location.Location.distanceBetween(ownerLocation.getLatitude(), ownerLocation.getLatitude(), currentUserLocation.getLatitude(), currentUserLocation.getLongitude(), distanceResults);

            // Score tracks close to the current location higher.
            double distanceKm = distanceResults[0] / 1000;
            if (distanceKm > 0.0) {
                // Use the inverse of the amortized distance.
                return distanceKm;
            } else {
                // Should rarely happen (distance is exactly 0).
                return Double.POSITIVE_INFINITY;
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    private Location getLocation(String username) {
        Cursor cursor = db.readUserRecord();
        if (cursor.moveToFirst()) {
            do {
                String usernameTemp = cursor.getString(cursor.getColumnIndex(UserDbHelper.KEY_USERNAME));

                if (usernameTemp.equals(username)) {
                    double latitude = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(UserDbHelper.KEY_LONGITUDE));
                    return new Location(latitude, longitude);
                }
            } while (cursor.moveToNext());
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class AllBooksHolder extends RecyclerView.ViewHolder {

        TextView txtBookTitleHolder;
        TextView txtBookAuthorHolder;
        TextView txtUserDistance;

        public AllBooksHolder(View itemView) {
            super(itemView);

            txtBookTitleHolder = (TextView) itemView.findViewById(R.id.txt_book_title_holder);
            txtBookAuthorHolder = (TextView) itemView.findViewById(R.id.txt_book_author_holder);
            txtUserDistance = (TextView) itemView.findViewById(R.id.txt_user_distance);
        }
    }
}
