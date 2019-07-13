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

    QueryDsl isNull();

    QueryDsl isNotNull();

    QueryDsl exists();

    QueryDsl any();

    QueryDsl all();

    QueryDsl not();

    QueryDsl and(String column);

    QueryDsl or(String column);

    QueryDsl orderBy(String... columns);

    QueryDsl asc();

    QueryDsl desc();

    QueryDsl limit(int value);

    QueryDsl limit(int offset, int limit);

    QueryDsl offset(int value);

    QueryDsl value(Object value);

    QueryDsl values(Object value, Object... values);

    QueryDsl column(String column);

    QueryDsl columns(String column, String... columns);

    QueryDsl innerJoin(String table);

    QueryDsl leftJoin(String table);

    QueryDsl rightJoin(String table);

    QueryDsl fullJoin(String table);

    QueryDsl crossJoin(String table);

    QueryDsl union();

    QueryDsl unionAll();

    QueryDsl on(String firstColumn, String secondColumn);

    QueryDsl on(String column);

    QueryDsl count(String column);

    QueryDsl avg(String column);

    QueryDsl sum(String column);

    QueryDsl min(String column);

    QueryDsl max(String column);

    QueryDsl groupBy(String... columns);

    QueryDsl having();

    QueryDsl as(String alias);

    QueryDsl openBracket();

    QueryDsl append(String custom);

    QueryDsl closeBracket();

    Query query();
}
