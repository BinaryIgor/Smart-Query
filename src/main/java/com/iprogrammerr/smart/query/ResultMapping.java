package com.iprogrammerr.smart.query;

import java.sql.ResultSet;

public interface ResultMapping<T> {
    T map(ResultSet result) throws Exception;
}
