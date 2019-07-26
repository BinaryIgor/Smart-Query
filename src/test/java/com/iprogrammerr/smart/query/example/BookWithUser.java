package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.mapping.Mapping;

import java.util.Objects;

public class BookWithUser {

    @Mapping(keys = "bid")
    private final int bookId;
    @Mapping(keys = "uid")
    private final int userId;
    @Mapping(keys = "title")
    private final String book;
    @Mapping(keys = "name")
    private final String user;

    public BookWithUser(int bookId, int userId, String book, String user) {
        this.bookId = bookId;
        this.userId = userId;
        this.book = book;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookWithUser that = (BookWithUser) o;
        return bookId == that.bookId &&
            userId == that.userId &&
            Objects.equals(book, that.book) &&
            Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, userId, book, user);
    }
}
