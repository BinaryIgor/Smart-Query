package com.iprogrammerr.smart.query;

import java.sql.ResultSet;

class Author {

    public static final String TABLE = "author";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ALIAS = "alias";

    public final long id;
    public final String name;
    public final String alias;

    public Author(long id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Author(ResultSet result) throws Exception {
        this(result.getLong(ID), result.getString(NAME), result.getString(ALIAS));
    }

    @Override
    public boolean equals(Object object) {
        try {
            Author other = (Author) object;
            return id == other.id && name.equals(other.name) && alias.equals(other.alias);
        } catch (Exception e) {
            return false;
        }
    }
}
