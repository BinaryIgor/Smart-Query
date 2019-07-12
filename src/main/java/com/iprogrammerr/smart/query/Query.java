package com.iprogrammerr.smart.query;

public interface Query {

    Query sql(String sql);

    Query set(Object value, Object... values);

    <T> T fetch(ResultMapping<T> mapping);

    void execute();

    long executeReturningId();
}
