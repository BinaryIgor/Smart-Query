package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;
import com.iprogrammerr.smart.query.mapping.clazz.ClassMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OneToManyMapping<T, R, P> implements ResultMapping<List<P>> {

    private final GroupPredicate<T> predicate;
    private final ResultMapping<T> firstMapping;
    private final ResultMapping<R> secondMapping;
    private final GroupMapping<P, T, R> groupMapping;

    public OneToManyMapping(GroupPredicate<T> predicate, ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, GroupMapping<P, T, R> groupMapping) {
        this.predicate = predicate;
        this.firstMapping = firstMapping;
        this.secondMapping = secondMapping;
        this.groupMapping = groupMapping;
    }

    public OneToManyMapping(GroupPredicate<T> predicate, Class<T> firstClass, Class<R> secondClass,
        GroupMapping<P, T, R> groupMapping) {
        this(predicate, new ClassMapping<>(firstClass), new ClassMapping<>(secondClass), groupMapping);
    }

    public OneToManyMapping(ResultMapping<T> firstMapping, ResultMapping<R> secondMapping,
        GroupMapping<P, T, R> groupMapping) {
        this(new EqualsPredicate<>(firstMapping), firstMapping, secondMapping, groupMapping);
    }

    public OneToManyMapping(Class<T> firstClass, Class<R> secondClass, GroupMapping<P, T, R> groupMapping) {
        this(new ClassMapping<>(firstClass), new ClassMapping<>(secondClass), groupMapping);
    }

    @Override
    public List<P> value(ResultSet result) throws Exception {
        List<P> results = new ArrayList<>();
        if (result.next()) {
            do {
                T one = firstMapping.value(result);
                List<R> many = new ArrayList<>();
                boolean next;
                do {
                    many.add(secondMapping.value(result));
                    next = result.next() && predicate.belongsTo(one, result);
                } while (next);
                results.add(groupMapping.value(one, many));
            } while (!result.isAfterLast());
        }
        return results;
    }
}
