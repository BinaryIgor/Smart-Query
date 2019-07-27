package com.iprogrammerr.smart.query.example.active;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;
import com.iprogrammerr.smart.query.example.table.Book;

public class BookRecord extends ActiveRecord<Integer, Book> {

    public BookRecord(QueryFactory factory, Integer id) {
        super(factory, Book.TABLE, new UpdateableColumn<>(Book.ID, id), Integer.class, true,
            new UpdateableColumn<>(Book.AUTHOR_ID), new UpdateableColumn<>(Book.TITLE));
    }

    public BookRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Book fetch() {
        return fetchQuery().fetch(r -> {
            r.next();
            return Book.fromResult(r);
        });
    }

    public BookRecord setAuthorId(Integer authorId) {
        set(Book.AUTHOR_ID, authorId);
        return this;
    }

    public BookRecord setTitle(String title) {
        set(Book.TITLE, title);
        return this;
    }
}