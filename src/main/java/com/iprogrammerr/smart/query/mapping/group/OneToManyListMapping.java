package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OneToManyListMapping<T, R, P> implements ResultMapping<List<P>> {

    private final OneToManyMapping<T, R, P> mapping;

    public OneToManyListMapping(GroupPredicate<T> predicate, ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, GroupMapping<P, T, R> groupMapping) {
        mapping = new OneToManyMapping<>(predicate, firstMapping, secondMapping, groupMapping);
    }

    @Override
    public List<P> value(ResultSet result) throws Exception {
        List<P> results = new ArrayList<>();
        if (result.next()) {
            do {
                results.add(mapping.value(result));
            } while (!result.isAfterLast());
        }
        return results;
    }
}
