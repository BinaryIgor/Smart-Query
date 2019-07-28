package com.iprogrammerr.smart.query.example.table;

public class UserBook {

    public static final String TABLE = "USER_BOOK";
    public static final String USER_ID = "user_id";
    public static final String BOOK_ID = "book_id";

    public final Integer userId;
    public final Integer bookId;

    public UserBook(Integer userId, Integer bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }
}