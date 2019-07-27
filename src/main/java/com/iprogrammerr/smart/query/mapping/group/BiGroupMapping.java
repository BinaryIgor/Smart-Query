package com.iprogrammerr.smart.query.mapping.group;

import java.util.List;
import java.util.Map;

public interface BiGroupMapping<T, S, R, P> {
    T value(S one, Map<R, List<P>> many);
}
