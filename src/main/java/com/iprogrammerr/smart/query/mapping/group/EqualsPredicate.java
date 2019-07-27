package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;

public class EqualsPredicate<T> implements GroupPredicate<T> {

    private final ResultMapping<T> mapping;

    public EqualsPredicate(ResultMapping<T> mapping) {
        this.mapping = mapping;
    }

    @Override
    public boolean belongsTo(T previous, ResultSet next) throws Exception {
        return previous.equals(mapping.value(next));
    }
}
