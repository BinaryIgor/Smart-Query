package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;
import com.iprogrammerr.smart.query.mapping.clazz.ClassMapping;
import com.iprogrammerr.smart.query.mapping.group.BiGroupMapping;
import com.iprogrammerr.smart.query.mapping.group.EqualsPredicate;
import com.iprogrammerr.smart.query.mapping.group.GroupMapping;
import com.iprogrammerr.smart.query.mapping.group.GroupPredicate;
import com.iprogrammerr.smart.query.mapping.group.ManyMapping;
import com.iprogrammerr.smart.query.mapping.group.OneToManyListMapping;
import com.iprogrammerr.smart.query.mapping.group.OneToManyMapping;
import com.iprogrammerr.smart.query.mapping.group.OneToManyOneToManyListMapping;
import com.iprogrammerr.smart.query.mapping.group.OneToManyOneToManyMapping;

import java.util.List;

public class Mappings {

    private Mappings() {
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
        return new OneToManyMapping<>(predicate, firstMapping, secondMapping, groupMapping, true);
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
        return new OneToManyListMapping<>(predicate, firstMapping, secondMapping, groupMapping);
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

    public static <T, S, R, P> ResultMapping<P> oneToManyOneToMany(GroupPredicate<T> firstPredicate,
        GroupPredicate<S> secondPredicate, ResultMapping<T> firstMapping, ResultMapping<S> secondMapping,
        ResultMapping<R> thirdMapping, BiGroupMapping<P, T, S, R> groupMapping) {
        return new OneToManyOneToManyMapping<>(firstPredicate, secondPredicate, firstMapping, secondMapping,
            thirdMapping, groupMapping, true);
    }

    public static <T, S, R, P> ResultMapping<P> oneToManyOneToMany(ResultMapping<T> firstMapping,
        ResultMapping<S> secondMapping, ResultMapping<R> thirdMapping, BiGroupMapping<P, T, S, R> groupMapping) {
        return oneToManyOneToMany(new EqualsPredicate<>(firstMapping), new EqualsPredicate<>(secondMapping),
            firstMapping, secondMapping, thirdMapping, groupMapping);
    }

    public static <T, S, R, P> ResultMapping<P> oneToManyOneToMany(GroupPredicate<T> firstPredicate,
        GroupPredicate<S> secondPredicate, Class<T> firstClass, Class<S> secondClass, Class<R> thirdClass,
        BiGroupMapping<P, T, S, R> groupMapping) {
        return oneToManyOneToMany(firstPredicate, secondPredicate, new ClassMapping<>(firstClass),
            new ClassMapping<>(secondClass), new ClassMapping<>(thirdClass), groupMapping);
    }

    public static <T, S, R, P> ResultMapping<P> oneToManyOneToMany(Class<T> firstClass, Class<S> secondClass,
        Class<R> thirdClass, BiGroupMapping<P, T, S, R> groupMapping) {
        return oneToManyOneToMany(new ClassMapping<>(firstClass), new ClassMapping<>(secondClass),
            new ClassMapping<>(thirdClass), groupMapping);
    }

    public static <T, S, R, P> ResultMapping<List<P>> listOfOneToManyOneToMany(GroupPredicate<T> firstPredicate,
        GroupPredicate<S> secondPredicate, ResultMapping<T> firstMapping, ResultMapping<S> secondMapping,
        ResultMapping<R> thirdMapping, BiGroupMapping<P, T, S, R> groupMapping) {
        return new OneToManyOneToManyListMapping<>(firstPredicate, secondPredicate, firstMapping, secondMapping,
            thirdMapping, groupMapping);
    }

    public static <T, S, R, P> ResultMapping<List<P>> listOfOneToManyOneToMany(ResultMapping<T> firstMapping,
        ResultMapping<S> secondMapping, ResultMapping<R> thirdMapping, BiGroupMapping<P, T, S, R> groupMapping) {
        return listOfOneToManyOneToMany(new EqualsPredicate<>(firstMapping), new EqualsPredicate<>(secondMapping),
            firstMapping, secondMapping, thirdMapping, groupMapping);
    }

    public static <T, S, R, P> ResultMapping<List<P>> listOfOneToManyOneToMany(GroupPredicate<T> firstPredicate,
        GroupPredicate<S> secondPredicate, Class<T> firstClass, Class<S> secondClass, Class<R> thirdClass,
        BiGroupMapping<P, T, S, R> groupMapping) {
        return listOfOneToManyOneToMany(firstPredicate, secondPredicate, new ClassMapping<>(firstClass),
            new ClassMapping<>(secondClass), new ClassMapping<>(thirdClass), groupMapping);
    }

    public static <T, S, R, P> ResultMapping<List<P>> listOfOneToManyOneToMany(Class<T> firstClass,
        Class<S> secondClass, Class<R> thirdClass, BiGroupMapping<P, T, S, R> groupMapping) {
        return listOfOneToManyOneToMany(new ClassMapping<>(firstClass), new ClassMapping<>(secondClass),
            new ClassMapping<>(thirdClass), groupMapping);
    }
}
