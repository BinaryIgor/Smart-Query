package com.iprogrammerr.smart.query.mapping.group;

import com.iprogrammerr.smart.query.ResultMapping;

import java.sql.ResultSet;
import java.util.Map;

public interface GroupsMapping<T, R> extends ResultMapping<Map<T, R>> {
    Map<T, R> value(ResultSet result) throws Exception;
}
