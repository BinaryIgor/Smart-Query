package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ManyMapping<T> implements ResultMapping<List<T>> {

    private final ResultMapping<T> mapping;

    public ManyMapping(ResultMapping<T> mapping) {
        this.mapping = mapping;
    }

    public ManyMapping(Class<T> clazz) {
        this(new ClassMapping<>(clazz));
    }

    @Override
    public List<T> value(ResultSet result) throws Exception {
        List<T> list = new ArrayList<>();
        while (result.next()) {
            list.add(mapping.value(result));
        }
        return list;
    }
}
