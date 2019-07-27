package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;
import com.iprogrammerr.smart.query.mapping.clazz.ClassMapping;
import com.iprogrammerr.smart.query.mapping.group.EqualsPredicate;
import com.iprogrammerr.smart.query.mapping.group.GroupMapping;
import com.iprogrammerr.smart.query.mapping.group.GroupPredicate;
import com.iprogrammerr.smart.query.mapping.group.ManyMapping;
import com.iprogrammerr.smart.query.mapping.group.ManyOneToManyMapping;
import com.iprogrammerr.smart.query.mapping.group.OneToManyMapping;

import java.util.List;

public class Mapping {

    private Mapping() {
    }

    public static <T> ResultMapping<T> ofClass(Class<T> clazz) {
        return new ClassMapping<>(clazz, true);
    }

    public static <T> ResultMapping<List<T>> listOfClass(Class<T> clazz) {
        return listOf(new ClassMapping<>(clazz));
    }

    public static <T> ResultMapping<List<T>> listOf(ResultMapping<T> mapping) {
        return new ManyMapping<>(mapping);
    }

    public static <T, R, P> ResultMapping<P> oneToMany(GroupPredicate<T> predicate, ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, GroupMapping<P, T, R> groupMapping) {
        return new OneToManyMapping<>(predicate, firstMapping, secondMapping, groupMapping);
    }

    public static <T, R, P> ResultMapping<P> oneToMany(ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, GroupMapping<P, T, R> groupMapping) {
        return oneToMany(new EqualsPredicate<>(firstMapping), firstMapping, secondMapping, groupMapping);
    }

    public static <T, R, P> ResultMapping<P> oneToMany(GroupPredicate<T> predicate, Class<T> firstClass,
        Class<R> secondClass, GroupMapping<P, T, R> groupMapping) {
        return oneToMany(predicate, new ClassMapping<>(firstClass), new ClassMapping<>(secondClass), groupMapping);
    }

    public static <T, R, P> ResultMapping<P> oneToMany(Class<T> firstClass, Class<R> secondClass,
        GroupMapping<P, T, R> groupMapping) {
        return oneToMany(new ClassMapping<>(firstClass), new ClassMapping<>(secondClass), groupMapping);
    }

    public static <T, R, P> ResultMapping<List<P>> listOfOneToMany(GroupPredicate<T> predicate,
        ResultMapping<T> firstMapping, ResultMapping<R> secondMapping, GroupMapping<P, T, R> groupMapping) {
        return new ManyOneToManyMapping<>(predicate, firstMapping, secondMapping, groupMapping);
    }

    public static <T, R, P> ResultMapping<List<P>> listOfOneToMany(ResultMapping<T> firstMapping,
        ResultMapping<R> secondMapping, GroupMapping<P, T, R> groupMapping) {
        return listOfOneToMany(new EqualsPredicate<>(firstMapping), firstMapping, secondMapping, groupMapping);
    }

    public static <T, R, P> ResultMapping<List<P>> listOfOneToMany(GroupPredicate<T> predicate, Class<T> firstClass,
        Class<R> secondClass, GroupMapping<P, T, R> groupMapping) {
        return listOfOneToMany(predicate, new ClassMapping<>(firstClass), new ClassMapping<>(secondClass),
            groupMapping);
    }

    public static <T, R, P> ResultMapping<List<P>> listOfOneToMany(Class<T> firstClass, Class<R> secondClass,
        GroupMapping<P, T, R> groupMapping) {
        return listOfOneToMany(new ClassMapping<>(firstClass), new ClassMapping<>(secondClass), groupMapping);
    }
}
