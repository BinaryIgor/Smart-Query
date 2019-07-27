package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ManyOneToManyMapping<T, R, P> implements ResultMapping<List<P>> {

    private final GroupPredicate<T> predicate;
    private final ResultMapping<T> firstMapping;
    private final ResultMapping<R> secondMapping;
    private final GroupMapping<P, T, R> groupMapping;

    public ManyOneToManyMapping(GroupPredicate<T> predicate, ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, GroupMapping<P, T, R> groupMapping) {
        this.predicate = predicate;
        this.firstMapping = firstMapping;
        this.secondMapping = secondMapping;
        this.groupMapping = groupMapping;
    }

    @Override
    public List<P> value(ResultSet result) throws Exception {
        List<P> results = new ArrayList<>();
        if (result.next()) {
            OneToManyMapping<T, R, P> mapping = new OneToManyMapping<>(predicate, firstMapping, secondMapping,
                groupMapping, false);
            do {
                results.add(mapping.value(result));
            } while (!result.isAfterLast());
        }
        return results;
    }
}
