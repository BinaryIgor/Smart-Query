package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.mapping.Mapping;

import java.sql.ResultSet;
import java.util.Objects;

public class Author {

    public static final String TABLE = "author";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ALIAS = "alias";
    public static final String ALIAS_ANONYM = "anonym";

    @Mapping(labels = "aid")
    public final long id;
    public final String name;
    @Mapping(labels = ALIAS_ANONYM)
    public final String alias;

    public Author(long id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public static Author fromResult(ResultSet result, String idLabel, String nameLabel, String aliasLabel)
        throws Exception {
        long id = result.getLong(idLabel);
        String name = result.getString(nameLabel);
        String alias = result.getString(aliasLabel);
        return new Author(id, name, alias);
    }

    public static Author fromResult(ResultSet result) throws Exception {
        return fromResult(result, ID, NAME, ALIAS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id == author.id &&
            Objects.equals(name, author.name) &&
            Objects.equals(alias, author.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, alias);
    }

    @Override
    public String toString() {
        return "Author{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", alias='" + alias + '\'' +
            '}';
    }
}
