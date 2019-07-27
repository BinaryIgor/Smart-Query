package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OneToManyOneToManyListMapping<T, S, R, P> implements ResultMapping<List<P>> {

    private final OneToManyOneToManyMapping<T, S, R, P> mapping;

    public OneToManyOneToManyListMapping(GroupPredicate<T> firstPredicate, GroupPredicate<S> secondPredicate,
        ResultMapping<T> firstMapping, ResultMapping<S> secondMapping, ResultMapping<R> thirdMapping,
        BiGroupMapping<P, T, S, R> groupMapping) {
        mapping = new OneToManyOneToManyMapping<>(firstPredicate, secondPredicate,
            firstMapping, secondMapping, thirdMapping, groupMapping);
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
