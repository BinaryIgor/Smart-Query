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
    public void buildsSingleFieldUpdate() {
        int rate = 2;
        QueryDsl dsl = dsl().update("city").set("rate", rate).where("id").equal().value(1);
        buildsProperQuery(dsl, "UPDATE city SET rate = ? WHERE id = ?", rate, 1);
    }

    @Test
    public void buildsUpdate() {
        int pages = 500;
        String author = "Anonymous Genius";
        String name = "Masterpiece";
        QueryDsl dsl = dsl().update("book").set("pages", pages).set("author", author)
            .where("name").equal().value(name);
        buildsProperQuery(dsl, "UPDATE book SET pages = ?, author = ? WHERE name = ?", pages, author, name);
    }

    @Test
    public void buildsDelete() {
        QueryDsl dsl = dsl().delete("a").where("name").notEqual().value("b");
        buildsProperQuery(dsl, "DELETE FROM a WHERE name != ?", "b");
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
    public void appendsIsNull() {
        appends(dsl().isNull(), "IS NULL");
    }

    @Test
    public void appendsIsNotNull() {
        appends(dsl().isNotNull(), "IS NOT NULL");
    }

    @Test
    public void appendsExists() {
        String subquery = "SELECT * FROM a WHERE b > 1";
        appends(dsl().exists(subquery), "EXISTS(" + subquery + ")");
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
    public void appendsEmptyOrderBy() {
        appends(dsl().orderBy(), "ORDER BY");
    }

    @Test
    public void appendsOrderBy() {
        appends(dsl().orderBy("a"), "ORDER BY a");
    }

    @Test
    public void appendsOrderByMultipleColumns() {
        appends(dsl().orderBy("a ASC", "b DESC"), "ORDER BY a ASC, b DESC");
    }

    @Test
    public void appendsAsc() {
        appends(dsl().asc(), "ASC");
    }

    @Test
    public void appendsDesc() {
        appends(dsl().desc(), "DESC");
    }

    @Test
    public void appendsLimit() {
        int limit = 3;
        buildsProperQuery(dsl().limit(limit), " LIMIT ?", limit);
    }

    @Test
    public void appendsOffset() {
        int offset = 2;
        buildsProperQuery(dsl().offset(offset), " OFFSET ?", offset);
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

    @Test
    public void appendsInnerJoin() {
        appends(dsl().innerJoin("author"), "INNER JOIN author");
    }

    @Test
    public void appendsLeftJoin() {
        appends(dsl().leftJoin("b"), "LEFT JOIN b");
    }

    @Test
    public void appendsRightJoin() {
        appends(dsl().rightJoin("c"), "RIGHT JOIN c");
    }

    @Test
    public void appendsFullJoin() {
        appends(dsl().fullJoin("d"), "FULL JOIN d");
    }

    @Test
    public void appendsCrossJoin() {
        appends(dsl().crossJoin("e"), "CROSS JOIN e");
    }

    @Test
    public void appendsDefaultOn() {
        appends(dsl().on("a", "b"), "ON a = b");
    }

    @Test
    public void appendsOn() {
        appends(dsl().on("a"), "ON a");
    }
}
