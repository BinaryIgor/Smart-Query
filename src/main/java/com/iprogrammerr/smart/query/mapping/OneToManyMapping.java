package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OneToManyMapping<T, R> implements GroupsMapping<T, List<R>> {

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
    public Map<T, List<R>> value(ResultSet result) throws Exception {
        Map<T, List<R>> groups = new LinkedHashMap<>();
        if (result.next()) {
            do {
                T key = firstMapping.value(result);
                List<R> values = new ArrayList<>();
                boolean next;
                do {
                    values.add(secondMapping.value(result));
                    next = result.next() && predicate.belongsTo(key, result);
                } while (next);
                groups.put(key, values);
            } while (!result.isAfterLast());
        }
        return groups;
    }
}
