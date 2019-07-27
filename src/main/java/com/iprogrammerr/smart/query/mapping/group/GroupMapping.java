package com.iprogrammerr.smart.query.mapping.group;

import java.util.List;

public interface GroupMapping<T, R, P> {
    T value(R one, List<P> many);
}
