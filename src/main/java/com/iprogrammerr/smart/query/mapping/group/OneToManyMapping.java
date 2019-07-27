package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;
import com.iprogrammerr.smart.query.mapping.clazz.ClassMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OneToManyMapping<T, R> implements ResultMapping<List<OneToMany<T, R>>> {

    private final GroupPredicate<T> predicate;
    private final ResultMapping<T> firstMapping;
    private final ResultMapping<R> secondMapping;

    public OneToManyMapping(GroupPredicate<T> predicate, ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping) {
        this.predicate = predicate;
        this.firstMapping = firstMapping;
        this.secondMapping = secondMapping;
    }

    public OneToManyMapping(GroupPredicate<T> predicate, Class<T> firstClass, Class<R> secondClass) {
        this(predicate, new ClassMapping<>(firstClass), new ClassMapping<>(secondClass));
    }

    public OneToManyMapping(ResultMapping<T> firstMapping, ResultMapping<R> secondMapping) {
        this(new EqualsPredicate<>(firstMapping), firstMapping, secondMapping);
    }

    public OneToManyMapping(Class<T> firstClass, Class<R> secondClass) {
        this(new ClassMapping<>(firstClass), new ClassMapping<>(secondClass));
    }

    @Override
    public List<OneToMany<T, R>> value(ResultSet result) throws Exception {
        List<OneToMany<T, R>> results = new ArrayList<>();
        if (result.next()) {
            do {
                T key = firstMapping.value(result);
                List<R> values = new ArrayList<>();
                boolean next;
                do {
                    values.add(secondMapping.value(result));
                    next = result.next() && predicate.belongsTo(key, result);
                } while (next);
                results.add(new OneToMany<>(key, values));
            } while (!result.isAfterLast());
        }
        return results;
    }
}
