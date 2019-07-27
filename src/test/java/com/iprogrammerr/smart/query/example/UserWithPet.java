package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.example.table.Pet;
import com.iprogrammerr.smart.query.example.table.User;
import com.iprogrammerr.smart.query.mapping.clazz.Embedded;

import java.util.Objects;

public class UserWithPet {

    @Embedded
    public final User user;
    @Embedded
    public final Pet pet;

    public UserWithPet(User user, Pet pet) {
        this.user = user;
        this.pet = pet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWithPet that = (UserWithPet) o;
        return Objects.equals(user, that.user) &&
            Objects.equals(pet, that.pet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, pet);
    }

    @Override
    public String toString() {
        return "UserWithPet{" +
            "user=" + user +
            ", pet=" + pet +
            '}';
    }
}
