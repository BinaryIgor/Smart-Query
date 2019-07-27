package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OneToManyMapping<T, R, P> implements ResultMapping<P> {

    private final GroupPredicate<T> predicate;
    private final ResultMapping<T> firstMapping;
    private final ResultMapping<R> secondMapping;
    private final GroupMapping<P, T, R> groupMapping;
    private final boolean moveResult;

    public OneToManyMapping(GroupPredicate<T> predicate, ResultMapping<T> firstMapping, ResultMapping<R> secondMapping,
        GroupMapping<P, T, R> groupMapping, boolean moveResult) {
        this.predicate = predicate;
        this.firstMapping = firstMapping;
        this.secondMapping = secondMapping;
        this.groupMapping = groupMapping;
        this.moveResult = moveResult;
    }

    public OneToManyMapping(GroupPredicate<T> predicate, ResultMapping<T> firstMapping, ResultMapping<R> secondMapping,
        GroupMapping<P, T, R> groupMapping) {
        this(predicate, firstMapping, secondMapping, groupMapping, false);
    }

    @Override
    public P value(ResultSet result) throws Exception {
        if (moveResult) {
            result.next();
        }
        T one = firstMapping.value(result);
        List<R> many = new ArrayList<>();
        boolean next;
        do {
            many.add(secondMapping.value(result));
            next = result.next() && predicate.belongsTo(one, result);
        } while (next);
        return groupMapping.value(one, many);
    }
}
