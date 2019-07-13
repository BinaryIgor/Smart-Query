package com.iprogrammerr.smart.query;

import java.sql.ResultSet;

public interface ResultMapping<T> {
    T value(ResultSet result) throws Exception;
}
