package com.iprogrammerr.smart.query;

public interface Query {

    Query sql(String sql);

    Query end();

    Query set(Object value, Object... values);

    QueryDsl dsl();

    <T> T fetch(ResultMapping<T> mapping);

    <T> T fetch(Class<T> clazz);

    void execute();

    long executeReturningId();

    void executeTransaction();
}
