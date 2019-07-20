package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class SmartQueryDslTest {

    private TestDatabase setup;

    @Before
    public void setup() {
        setup = new TestDatabase();
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
    public void buildsAliasedSelect() {
        String query = toString(dsl().select().as("a", "id", "name", "surname")
            .from("author").as("a"));
        MatcherAssert.assertThat(query, Matchers.equalTo(
            "SELECT id AS aid, name AS aname, surname AS asurname FROM author AS a"));
    }

    @Test
    public void buildsInsert() {
        String place = "Somewhere";
        int rate = 5;
        QueryDsl dsl = dsl()
            .insertInto("library")
            .columns("place", "rate")
            .values(place, rate);
        buildsProperQuery(dsl, "INSERT INTO library(place, rate) VALUES(?, ?)", place, rate);
    }

    private void buildsProperQuery(QueryDsl dsl, String template, Object... values) {
        SmartQuery query = queryFromDsl(dsl);
        MatcherAssert.assertThat(query.template(), Matchers.equalTo(template));
        MatcherAssert.assertThat(query.values(), Matchers.contains(values));
    }

    private SmartQuery queryFromDsl(QueryDsl dsl) {
        return (SmartQuery) dsl.query();
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
        QueryDsl dsl = dsl()
            .update("book")
            .set("pages", pages).set("author", author)
            .where("name").equal().value(name);
        buildsProperQuery(dsl, "UPDATE book SET pages = ?, author = ? WHERE name = ?", pages, author, name);
    }

    @Test
    public void buildsDelete() {
        QueryDsl dsl = dsl().delete("a").where("name").notEqual().value("b");
        buildsProperQuery(dsl, "DELETE FROM a WHERE name != ?", "b");
    }

    @Test
    public void buildsSelectInto() {
        String value = "Germany";
        QueryDsl dsl = dsl()
            .selectAll().append(" INTO CustomersGermany").from("Customers")
            .where("Country").equal().value(value);
        String template = "SELECT * INTO CustomersGermany FROM Customers WHERE Country = ?";
        buildsProperQuery(dsl, template, value);
    }

    @Test
    public void appendsWhere() {
        appends(dsl().where("name"), "WHERE name");
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
        appends(dsl().exists(), "EXISTS");
    }

    @Test
    public void appendsAny() {
        appends(dsl().any(), "ANY");
    }

    @Test
    public void appendsAll() {
        appends(dsl().all(), "ALL");
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
    public void appendsOrWitColumn() {
        appends(dsl().or("b"), "OR b");
    }

    @Test
    public void appendsAnd() {
        appends(dsl().and(), "AND");
    }

    @Test
    public void appendsAndWithColumn() {
        appends(dsl().and("a"), "AND a");
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
    public void appendsLimitWithOffset() {
        int offset = 1;
        int limit = 5;
        buildsProperQuery(dsl().limit(offset, limit), " LIMIT ?, ?", offset, limit);
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
    public void appendsColumn() {
        appends(dsl().column("a"), "a");
    }

    @Test
    public void appendsJoin() {
        appends(dsl().join("book"), "JOIN book");
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

    @Test
    public void appendsUnion() {
        appends(dsl().union(), "UNION ");
    }

    @Test
    public void appendsUnionAll() {
        appends(dsl().unionAll(), "UNION ALL ");
    }

    @Test
    public void appendsCount() {
        appends(dsl().count("i"), "COUNT(i)");
    }

    @Test
    public void appendsAvg() {
        appends(dsl().avg("a"), "AVG(a)");
    }

    @Test
    public void appendsSum() {
        appends(dsl().sum("*"), "SUM(*)");
    }

    @Test
    public void appendsMin() {
        appends(dsl().min("x"), "MIN(x)");
    }

    @Test
    public void appendsMax() {
        appends(dsl().max("y"), "MAX(y)");
    }

    @Test
    public void appendsNoArgsFunction() {
        appends(dsl().function("RAND"), "RAND()");
    }

    @Test
    public void appendsEmptyGroupBy() {
        appends(dsl().groupBy(), "GROUP BY");
    }

    @Test
    public void appendsGroupBy() {
        appends(dsl().groupBy("g"), "GROUP BY g");
    }

    @Test
    public void appendsGroupByMultipleColumns() {
        appends(dsl().groupBy("a", "b", "c"), "GROUP BY a, b, c");
    }

    @Test
    public void appendsHaving() {
        appends(dsl().having().count("*"), "HAVING COUNT(*)");
    }

    @Test
    public void appendsAlias() {
        appends(dsl().as("crypto"), "AS crypto");
    }

    @Test
    public void buildsSubquery() {
        int quantity = 100;
        QueryDsl dsl = dsl()
            .select("ProductName").from("Product").where("Id").in()
            .openBracket()
            .select("ProductId").from("OrderItem").where("Quantity").greater().value(quantity)
            .closeBracket();
        String template = "SELECT ProductName FROM Product WHERE Id IN(SELECT ProductId FROM OrderItem WHERE Quantity > ?)";
        buildsProperQuery(dsl, template, quantity);
    }

    @Test
    public void buildsQueryWithAppended() {
        String custom = "SELECT CURRENT_TIMESTAMP";
        QueryDsl dsl = dsl().selectAll().from("a").unionAll().append(custom);
        MatcherAssert.assertThat(toString(dsl), Matchers.equalTo("SELECT * FROM a UNION ALL " + custom));
    }

    @Test
    public void buildsQueryWithFunction() {
        String firstArg = "Igor";
        int secondArg = 3;
        QueryDsl dsl = dsl()
            .select("name").from("author")
            .where("name").equal().function("RIGHT", "Igor", 3);
        buildsProperQuery(dsl, "SELECT name FROM author WHERE name = RIGHT(?, ?)", firstArg, secondArg);
    }
}
