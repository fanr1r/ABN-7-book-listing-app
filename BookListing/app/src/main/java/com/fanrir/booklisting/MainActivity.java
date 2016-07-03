package com.fanrir.booklisting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /** List that stores the books */
    private BookAdapter mBookAdapter;

    /** List that stores the books */
    private ListView mListView;

    /** The keyword entered for book search */
    private String mKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create book list where books will be stored
        ArrayList<Book> books = new ArrayList<>();

        //create Adapter for book list
        mBookAdapter = new BookAdapter(this, books);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) findViewById(R.id.published_books_list_view);
        View emptyView = findViewById(R.id.listview_books_empty);
        mListView.setEmptyView(emptyView);
        mListView.setAdapter(mBookAdapter);

        final EditText keywordEditText = (EditText) findViewById(R.id.keyword_edit_text);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKeyword = keywordEditText.getText().toString();
                searchBooks();
            }
        });
    }

    private void searchBooks() {
        FetchBooksTask bookListTask = new FetchBooksTask(this, this);
        bookListTask.execute(mKeyword);
    }

    public void refreshBookList(ArrayList<Book> result) {
        mBookAdapter.clear();
        for(Book book : result) {
            mBookAdapter.add(book);
        }
    }
}
