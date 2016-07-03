package com.fanrir.booklisting;

/**
 * Created by Eisdrachl on 03.07.2016.
 */
public class Book {

    /** The author of the book */
    private String mAuthors;

    /** The book title */
    private String mTitle;

    /**
     * Create a new Book object.
     *
     * @param authors is the author of the book
     * @param title is the title of the book
     */
    public Book(String authors, String title) {
        mAuthors = authors;
        mTitle = title;
    }

    /**
     * Get the author of the book.
     */
    public String getAuthors() {
        return mAuthors;
    }

    /**
     * Get the title of the bool.
     */
    public String getTitle() {
        return mTitle;
    }



}
