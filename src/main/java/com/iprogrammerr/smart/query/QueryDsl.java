package com.iprogrammerr.smart.query;

public interface QueryDsl {

    QueryDsl select(String... columns);

    QueryDsl selectDistinct(String... columns);

    QueryDsl selectAll();

    QueryDsl from(String table);

    QueryDsl where(String column);

    QueryDsl equal();

    QueryDsl notEqual();

    QueryDsl less();

    QueryDsl lessEqual();

    QueryDsl between();

    QueryDsl notBetween();

    QueryDsl in();

    QueryDsl notIn();

    QueryDsl like(String pattern);

    QueryDsl notLike(String pattern);

    QueryDsl greater();

    QueryDsl greaterEqual();

    QueryDsl value(Object value);

    QueryDsl column(String column);

    Query build();
}
