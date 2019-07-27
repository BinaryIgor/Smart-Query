package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneToManyOneToManyMapping<T, S, R, P> implements ResultMapping<P> {

    private final GroupPredicate<T> firstPredicate;
    private final GroupPredicate<S> secondPredicate;
    private final ResultMapping<T> firstMapping;
    private final ResultMapping<S> secondMapping;
    private final ResultMapping<R> thirdMapping;
    private final BiGroupMapping<P, T, S, R> groupMapping;
    private final boolean initResult;

    public OneToManyOneToManyMapping(GroupPredicate<T> firstPredicate, GroupPredicate<S> secondPredicate,
        ResultMapping<T> firstMapping, ResultMapping<S> secondMapping, ResultMapping<R> thirdMapping,
        BiGroupMapping<P, T, S, R> groupMapping, boolean initResult) {
        this.firstPredicate = firstPredicate;
        this.secondPredicate = secondPredicate;
        this.firstMapping = firstMapping;
        this.secondMapping = secondMapping;
        this.thirdMapping = thirdMapping;
        this.groupMapping = groupMapping;
        this.initResult = initResult;
    }

    public OneToManyOneToManyMapping(GroupPredicate<T> firstPredicate, GroupPredicate<S> secondPredicate,
        ResultMapping<T> firstMapping, ResultMapping<S> secondMapping, ResultMapping<R> thirdMapping,
        BiGroupMapping<P, T, S, R> groupMapping) {
        this(firstPredicate, secondPredicate, firstMapping, secondMapping, thirdMapping, groupMapping, false);
    }

    @Override
    public P value(ResultSet result) throws Exception {
        if (initResult) {
            result.next();
        }
        T one = firstMapping.value(result);
        Map<S, List<R>> many = new HashMap<>();
        boolean outerNext;
        do {
            S innerOne = secondMapping.value(result);
            List<R> innerMany = new ArrayList<>();
            boolean innerNext;
            do {
                innerMany.add(thirdMapping.value(result));
                innerNext = result.next() && secondPredicate.belongsTo(innerOne, result);
            } while (innerNext);
            many.put(innerOne, innerMany);
            outerNext = !result.isAfterLast() && firstPredicate.belongsTo(one, result);
        } while (outerNext);
        return groupMapping.value(one, many);
    }
}
