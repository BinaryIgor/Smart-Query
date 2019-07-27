package com.iprogrammerr.smart.query.example.table;

import com.iprogrammerr.smart.query.mapping.clazz.Mapping;

import java.sql.ResultSet;
import java.util.Objects;

public class Pet {

    public static final String TABLE = "PET";
    public static final String USER_ID = "user_id";
    public static final String NAME = "name";

    @Mapping(labels = {"p_id", USER_ID})
    public final Integer userId;
    @Mapping(labels = "p_name")
    public final String name;

    public Pet(Integer userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public static Pet fromResult(ResultSet result, String userIdLabel, String nameLabel) throws Exception {
        Integer userId = result.getInt(userIdLabel);
        String name = result.getString(nameLabel);
        return new Pet(userId, name);
    }

    public static Pet fromResult(ResultSet result) throws Exception {
        return fromResult(result, USER_ID, NAME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(userId, pet.userId) &&
            Objects.equals(name, pet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }

    @Override
    public String toString() {
        return "Pet{" +
            "userId=" + userId +
            ", name='" + name + '\'' +
            '}';
    }
}