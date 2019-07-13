package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class SmartQueryDslTest {

    private TestDatabaseSetup setup;

    @Before
    public void setup() {
        setup = new TestDatabaseSetup();
        setup.setup();
    }

    @Test
    public void buildsSelect() {
        String query = toString(dsl().select("a", "b").from("author"));
        MatcherAssert.assertThat(query, Matchers.equalTo("SELECT a, b FROM author"));
    }

    private SmartQueryDsl dsl() {
        return (SmartQueryDsl) setup.query().dsl();
    }

    private String toString(QueryDsl dsl) {
        return SmartQueryDsl.class.cast(dsl).template();
    }

    @Test
    public void buildsSelectAll() {
        String query = toString(dsl().selectAll().from("book"));
        MatcherAssert.assertThat(query, Matchers.equalTo("SELECT * FROM book"));
    }

    @Test
    public void buildsSelectDistinct() {
        String query = toString(dsl().selectDistinct("name").from("pet"));
        MatcherAssert.assertThat(query, Matchers.equalTo("SELECT DISTINCT name FROM pet"));
    }

    @Test
    public void buildsInsert() {
        String place = "Somewhere";
        int rate = 5;
        QueryDsl dsl = dsl().insertInto("library").columns("place", "rate")
            .values(place, rate);
        buildsProperQuery(dsl, "INSERT INTO library(place, rate) VALUES(?, ?)", place, rate);
    }

    private void buildsProperQuery(QueryDsl dsl, String template, Object... values) {
        SmartQuery query = queryFromDsl(dsl);
        MatcherAssert.assertThat(query.template(), Matchers.equalTo(template));
        MatcherAssert.assertThat(query.values(), Matchers.contains(values));
    }

    private SmartQuery queryFromDsl(QueryDsl dsl) {
        return (SmartQuery) dsl.build();
    }

    @Test
    public void appendsWhere() {
        MatcherAssert.assertThat(toString(dsl().where("name")), Matchers.endsWith("WHERE name"));
    }

    @Test
    public void appendsEqual() {
        appends(dsl().equal(), "=");
    }

    private void appends(QueryDsl dsl, String value) {
        MatcherAssert.assertThat(toString(dsl), Matchers.endsWith(" " + value));
    }

    @Test
    public void appendsNotEqual() {
        appends(dsl().notEqual(), "!=");
    }

    @Test
    public void appendsLess() {
        appends(dsl().less(), "<");
    }

    @Test
    public void appendsLessEqual() {
        appends(dsl().lessEqual(), "<=");
    }

    @Test
    public void appendsGreater() {
        appends(dsl().greater(), ">");
    }

    @Test
    public void appendsGreaterEqual() {
        appends(dsl().greaterEqual(), ">=");
    }

    @Test
    public void appendsBetween() {
        appends(dsl().between(), "BETWEEN");
    }

    @Test
    public void appendsIn() {
        appends(dsl().in(), "IN");
    }

    @Test
    public void appendsLikePattern() {
        String pattern = "%p__";
        appends(dsl().like(pattern), String.format("LIKE '%s'", pattern));
    }

    @Test
    public void appendsNot() {
        appends(dsl().not(), "NOT");
    }

    @Test
    public void appendsOr() {
        appends(dsl().or(), "OR");
    }

    @Test
    public void appendsAnd() {
        appends(dsl().and(), "AND");
    }

    @Test
    public void appendsValue() {
        String value = "None";
        QueryDsl dsl = dsl().value(value);
        buildsProperQuery(dsl, " ?", value);
    }

    @Test
    public void appendsValues() {
        int first = 1;
        double second = 3.3;
        String third = "Third";
        QueryDsl dsl = dsl().values(first, second, third);
        buildsProperQuery(dsl, "(?, ?, ?)", first, second, third);
    }

    @Test
    public void appendsSubquery() {
        String subquery = "SELECT * FROM a";
        appends(dsl().subquery(subquery), "(" + subquery + ")");
    }

    @Test
    public void appendsColumn() {
        MatcherAssert.assertThat(toString(dsl().column("a")), Matchers.endsWith("a"));
    }
}
