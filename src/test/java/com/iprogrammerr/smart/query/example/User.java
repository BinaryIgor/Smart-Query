package com.iprogrammerr.smart.query.example;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {

    public static final String TABLE = "USER";
    public static final String ID = "id";
    public static final String NAME = "name";

    public final int id;
    public final String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static User fromResult(ResultSet result, String idLabel, String nameLabel) throws Exception {
        int id = result.getInt(idLabel);
        String name = result.getString(nameLabel);
        return new User(id, name);
    }

    public static User fromResult(ResultSet result) throws Exception {
        return fromResult(result, ID, NAME);
    }

    public static List<User> listFromResult(ResultSet result, String idLabel, String nameLabel) throws Exception {
        List<User> list = new ArrayList<>();
        do {
            list.add(fromResult(result, idLabel, nameLabel));
        } while (result.next());
        return list;
    }

    public static List<User> listFromResult(ResultSet result) throws Exception {
        return listFromResult(result, ID, NAME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
            Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}