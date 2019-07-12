package com.iprogrammerr.smart.query;

import java.sql.ResultSet;

class Author {

    public final long id;
    public final String name;
    public final String alias;

    public Author(long id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Author(ResultSet result) throws Exception {
        this(result.getLong("id"), result.getString("name"), result.getString("alias"));
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
