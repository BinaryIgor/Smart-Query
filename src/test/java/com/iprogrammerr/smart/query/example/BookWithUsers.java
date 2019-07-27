package com.iprogrammerr.smart.query.example;

import java.util.List;
import java.util.Objects;

public class BookWithUsers {

    public final Book book;
    public final List<User> users;

    public BookWithUsers(Book book, List<User> users) {
        this.book = book;
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookWithUsers that = (BookWithUsers) o;
        return Objects.equals(book, that.book) &&
            Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, users);
    }
}
