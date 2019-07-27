package com.iprogrammerr.smart.query.mapping.group;

import java.util.List;

public class OneToMany<T, R> {

    public final T one;
    public final List<R> many;

    public OneToMany(T one, List<R> many) {
        this.one = one;
        this.many = many;
    }
}
