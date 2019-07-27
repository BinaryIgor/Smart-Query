package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;
import com.iprogrammerr.smart.query.mapping.clazz.ClassMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OneToManyOneToManyListMapping<T, S, R, P> implements ResultMapping<List<P>> {

    private final GroupPredicate<T> firstPredicate;
    private final GroupPredicate<S> secondPredicate;
    private final ResultMapping<T> firstMapping;
    private final ResultMapping<S> secondMapping;
    private final ResultMapping<R> thirdMapping;
    private final BiGroupMapping<P, T, S, R> groupMapping;

    public OneToManyOneToManyListMapping(GroupPredicate<T> firstPredicate,
        GroupPredicate<S> secondPredicate, ResultMapping<T> firstMapping,
        ResultMapping<S> secondMapping, ResultMapping<R> thirdMapping,
        BiGroupMapping<P, T, S, R> groupMapping) {
        this.firstPredicate = firstPredicate;
        this.secondPredicate = secondPredicate;
        this.firstMapping = firstMapping;
        this.secondMapping = secondMapping;
        this.thirdMapping = thirdMapping;
        this.groupMapping = groupMapping;
    }

    public OneToManyOneToManyListMapping(ResultMapping<T> firstMapping, ResultMapping<S> secondMapping,
        ResultMapping<R> thirdMapping, BiGroupMapping<P, T, S, R> groupMapping) {
        this(new EqualsPredicate<>(firstMapping), new EqualsPredicate<>(secondMapping), firstMapping, secondMapping,
            thirdMapping, groupMapping);
    }

    public OneToManyOneToManyListMapping(GroupPredicate<T> firstPredicate, GroupPredicate<S> secondPredicate,
        Class<T> firstClass, Class<S> secondClass, Class<R> thirdClass, BiGroupMapping<P, T, S, R> groupMapping) {
        this(firstPredicate, secondPredicate, new ClassMapping<>(firstClass), new ClassMapping<>(secondClass),
            new ClassMapping<>(thirdClass), groupMapping);
    }

    public OneToManyOneToManyListMapping(Class<T> firstClass, Class<S> secondClass, Class<R> thirdClass,
        BiGroupMapping<P, T, S, R> groupMapping) {
        this(new ClassMapping<>(firstClass), new ClassMapping<>(secondClass), new ClassMapping<>(thirdClass),
            groupMapping);
    }

    @Override
    public List<P> value(ResultSet result) throws Exception {
        List<P> results = new ArrayList<>();
        if (!result.next()) {
            return results;
        }
        OneToManyOneToManyMapping<T, S, R, P> mapping = new OneToManyOneToManyMapping<>(firstPredicate, secondPredicate,
            firstMapping, secondMapping, thirdMapping, groupMapping, false);
        do {
            results.add(mapping.value(result));
        } while (!result.isAfterLast());
        return results;
    }
}
