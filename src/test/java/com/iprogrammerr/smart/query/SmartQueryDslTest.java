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
    public void appendsWhere() {
        MatcherAssert.assertThat(toString(dsl().where("name")), Matchers.endsWith("WHERE name"));
    }

    @Test
    public void appendsEqual() {
        appendsOperator(dsl().equal(), "=");
    }

    private void appendsOperator(QueryDsl dsl, String operator) {
        MatcherAssert.assertThat(toString(dsl), Matchers.endsWith(operator));
    }

    @Test
    public void appendsNotEqual() {
        appendsOperator(dsl().notEqual(), "!=");
    }

    @Test
    public void appendsLess() {
        appendsOperator(dsl().less(), "<");
    }

    @Test
    public void appendsLessEqual() {
        appendsOperator(dsl().lessEqual(), "<=");
    }

    @Test
    public void appendsGreater() {
        appendsOperator(dsl().greater(), ">");
    }

    @Test
    public void appendsGreaterEqual() {
        appendsOperator(dsl().greaterEqual(), ">=");
    }

    @Test
    public void appendsValue() {
        String value = "None";
        SmartQuery query = (SmartQuery) dsl().value(value).build();
        MatcherAssert.assertThat(query.template(), Matchers.endsWith("?"));
        MatcherAssert.assertThat(query.values(), Matchers.contains(value));
    }

    @Test
    public void appendsColumn() {
        MatcherAssert.assertThat(toString(dsl().column("a")), Matchers.endsWith("a"));
    }
}
