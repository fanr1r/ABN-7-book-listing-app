package com.fanrir.booklisting;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Eisdrachl on 03.07.2016.
 */
public class FetchBooksTask extends AsyncTask<String, Void, ArrayList<Book>> {

    public MainActivity mMainActivity;

    private final String LOG_TAG = FetchBooksTask.class.getSimpleName();

    private final Context mContext;

    public FetchBooksTask(Context context, MainActivity mainActivity) {
        mContext = context;
        mMainActivity = mainActivity;
    }

    /**
     * Takes the String representing the complete book list in JSON Format and
     * pull out the data we need to construct the Strings needed for the book list.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private ArrayList<Book> getBookDataFromJson(String booksJsonStr)
            throws JSONException {

        // The bookList to fill with results
        ArrayList<Book> bookList = new ArrayList();

        // The key to pass on the JSON Object
        final String BOOK_ITEMS = "items";
        final String BOOK_VOLUME_INFO = "volumeInfo";
        final String BOOK_TITLE = "title";
        final String BOOK_AUTHOR = "authors";

        try {
            JSONObject booksJson = new JSONObject(booksJsonStr);
            JSONArray itemsArray = booksJson.getJSONArray(BOOK_ITEMS);

            for(int i = 0; i < itemsArray.length(); i++) {

                // These are the values that will be collected.
                String title = "";
                String authors = "";

                // Get the JSON object representing a book
                JSONObject bookInfo = itemsArray.getJSONObject(i);
                JSONObject volumeInfoJson = bookInfo.getJSONObject(BOOK_VOLUME_INFO);

                title = volumeInfoJson.getString(BOOK_TITLE);

                JSONArray authorsArray = volumeInfoJson.getJSONArray(BOOK_AUTHOR);
                for (int j = 0; j < authorsArray.length(); j++) {
                    if (j == 0) {
                        authors += authorsArray.getString(j);
                    } else {
                        authors += ", " + authorsArray.getString(j);
                    }
                }

                // Add book entry for bookList if not already in bookList
                Book book = new Book(authors, title);
                boolean isBookInList = false;
                for(Book b : bookList) {
                    if (b.getTitle().equals(title)) {
                        isBookInList = true;
                    }
                }
                if (!isBookInList) {
                    bookList.add(book);
                }
            }

            Log.d(LOG_TAG, "FetchBooksTask Complete.");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return bookList;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String keywordQuery = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String booksJsonStr = null;

        int numMaxResults = 15;
        String order = "newest";

        try {
            // Construct the URL for the Google Books API query
            // https://developers.google.com/books/docs/v1/getting_started#intro
            final String GOOGLE_BOOKS_BASE_URL =
                    "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";
            final String MAX_PARAM = "maxResults";
            final String ORDER_PARAM = "orderBy";

            Uri builtUri = Uri.parse(GOOGLE_BOOKS_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(MAX_PARAM, Integer.toString(numMaxResults))
                    .appendQueryParameter(ORDER_PARAM, order)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to Google Books API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            booksJsonStr = buffer.toString();
            return getBookDataFromJson(booksJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        // This will only happen if there was an error getting or parsing the book list.
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Book> result) {
        if (result != null) {
            mMainActivity.refreshBookList(result);
        }
    }
}
