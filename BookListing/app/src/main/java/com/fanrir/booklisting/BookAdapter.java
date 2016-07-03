package com.fanrir.booklisting;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Eisdrachl on 03.07.2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book book = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_book, parent, false);
        }

        TextView miwokTextView = (TextView) convertView.findViewById(R.id.title_text_view);
        miwokTextView.setText(book.getTitle());

        TextView translationTextView = (TextView) convertView.findViewById(R.id.author_text_view);
        translationTextView.setText(book.getAuthors());

        return convertView;
    }
}
