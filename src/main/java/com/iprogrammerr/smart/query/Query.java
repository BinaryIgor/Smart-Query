package com.iprogrammerr.smart.query;

import java.util.Collection;

public interface Query {

    Query sql(String sql);

    Query end();

    Query set(Object value, Object... values);

    QueryDsl dsl();

    <T> T fetch(ResultMapping<T> mapping);

    <T> T fetch(Class<T> clazz);

    <T> void fetchInto(Collection<T> container, ResultMapping<T> mapping);

    <T> void fetchInto(Collection<T> container, Class<T> clazz);

    void execute();

    long executeReturningId();

    void executeTransaction();
}
