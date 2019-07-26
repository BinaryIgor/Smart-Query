package com.iprogrammerr.smart.query.example;

import java.util.Objects;

public class BookWithAuthor {

    public final String author;
    public final String title;

    public BookWithAuthor(String author, String title) {
        this.author = author;
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookWithAuthor that = (BookWithAuthor) o;
        return Objects.equals(author, that.author) &&
            Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title);
    }
}
