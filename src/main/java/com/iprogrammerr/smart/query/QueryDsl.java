package com.iprogrammerr.smart.query;

public interface QueryDsl {

    QueryDsl select(String... columns);

    QueryDsl selectDistinct(String... columns);

    QueryDsl selectAll();

    QueryDsl from(String table);

    QueryDsl insertInto(String table);

    QueryDsl update(String table);

    QueryDsl set(String column, Object value);

    QueryDsl delete(String table);

    QueryDsl where(String column);

    QueryDsl equal();

    QueryDsl notEqual();

    QueryDsl less();

    QueryDsl lessEqual();

    QueryDsl greater();

    QueryDsl greaterEqual();

    QueryDsl between();

    QueryDsl in();

    QueryDsl like(String pattern);

    QueryDsl not();

    QueryDsl and();

    QueryDsl or();

    QueryDsl orderBy(String... columns);

    QueryDsl asc();

    QueryDsl desc();

    QueryDsl limit(int value);

    QueryDsl offset(int value);

    QueryDsl value(Object value);

    QueryDsl values(Object value, Object... values);

    QueryDsl column(String column);

    QueryDsl columns(String column, String... columns);

    QueryDsl subquery(String subquery);

    Query build();
}
