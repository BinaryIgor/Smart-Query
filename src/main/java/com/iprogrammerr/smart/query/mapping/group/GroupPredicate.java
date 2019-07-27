package com.iprogrammerr.smart.query.mapping.group;

import java.sql.ResultSet;

public interface GroupPredicate<T> {
    boolean belongsTo(T previous, ResultSet next) throws Exception;
}
