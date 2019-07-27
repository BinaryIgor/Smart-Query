package com.iprogrammerr.smart.query.example.active;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;
import com.iprogrammerr.smart.query.example.table.Pet;

public class PetRecord extends ActiveRecord<Integer, Pet> {

    public PetRecord(QueryFactory factory, Integer userId) {
        super(factory, Pet.TABLE, new UpdateableColumn<>(Pet.USER_ID, userId), Integer.class, false,
            new UpdateableColumn<>(Pet.NAME));
    }

    public PetRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Pet fetch() {
        return fetchQuery().fetch(r -> {
            r.next();
            return Pet.fromResult(r);
        });
    }

    public PetRecord setUserId(Integer userId) {
        set(Pet.USER_ID, userId);
        return this;
    }

    public PetRecord setName(String name) {
        set(Pet.NAME, name);
        return this;
    }
}