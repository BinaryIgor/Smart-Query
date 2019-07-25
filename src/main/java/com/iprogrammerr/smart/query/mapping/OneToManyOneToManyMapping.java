package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OneToManyOneToManyMapping<T, R, P> implements GroupsMapping<T, Map<R, List<P>>> {

    private final GroupPredicate<T> firstPredicate;
    private final GroupPredicate<R> secondPredicate;
    private final ResultMapping<T> firstMapping;
    private final ResultMapping<R> secondMapping;
    private final ResultMapping<P> thirdMapping;

    public OneToManyOneToManyMapping(GroupPredicate<T> firstPredicate,
        GroupPredicate<R> secondPredicate, ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, ResultMapping<P> thirdMapping) {
        this.firstPredicate = firstPredicate;
        this.secondPredicate = secondPredicate;
        this.firstMapping = firstMapping;
        this.secondMapping = secondMapping;
        this.thirdMapping = thirdMapping;
    }

    public OneToManyOneToManyMapping(GroupPredicate<T> firstPredicate, ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, ResultMapping<P> thirdMapping) {
        this(firstPredicate, new EqualsPredicate<>(secondMapping), firstMapping, secondMapping, thirdMapping);
    }

    public OneToManyOneToManyMapping(ResultMapping<T> firstMapping, ResultMapping<R> secondMapping,
        ResultMapping<P> thirdMapping) {
        this(new EqualsPredicate<>(firstMapping), firstMapping, secondMapping, thirdMapping);
    }

    @Override
    public Map<T, Map<R, List<P>>> value(ResultSet result) throws Exception {
        if (!result.next()) {
            return new LinkedHashMap<>();
        }
        Map<T, Map<R, List<P>>> groups = new LinkedHashMap<>();
        do {
            T outerKey = firstMapping.value(result);
            Map<R, List<P>> outerValues = new LinkedHashMap<>();
            boolean outerNext;
            do {
                R outerValue = secondMapping.value(result);
                List<P> innerValues = new ArrayList<>();
                boolean innerNext;
                do {
                    innerValues.add(thirdMapping.value(result));
                    innerNext = result.next() && secondPredicate.belongsTo(outerValue, result);
                } while (innerNext);
                outerValues.put(outerValue, innerValues);
                outerNext = !result.isAfterLast() && firstPredicate.belongsTo(outerKey, result);
            } while (outerNext);
            groups.put(outerKey, outerValues);
        } while (!result.isAfterLast());
        return groups;
    }
}
