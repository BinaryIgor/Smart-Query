package com.iprogrammerr.smart.query.mapping;

import java.sql.ResultSet;
import java.util.Map;

public interface GroupsMapping<T, R> {
    Map<T, R> value(ResultSet result) throws Exception;
}
