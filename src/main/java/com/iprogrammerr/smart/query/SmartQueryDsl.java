package com.iprogrammerr.smart.query;

import java.util.List;

public class SmartQueryDsl implements QueryDsl {

    private static final String VALUE_PLACEHOLDER = "?";
    private static final String COMMA = ",";
    private static final String SPACE = " ";
    private static final String BRACKET_START = "(";
    private static final String BRACKET_END = ")";
    private final Query query;
    private final StringBuilder template;
    private final List<Object> values;
    private boolean insert;
    private boolean update;

    public SmartQueryDsl(Query query, StringBuilder template, List<Object> values) {
        this.query = query;
        this.template = template;
        this.values = values;
        this.insert = this.update = false;
    }

    @Override
    public QueryDsl select(String... columns) {
        return select(false, columns);
    }

    private QueryDsl select(boolean distinct, String... columns) {
        template.append("SELECT");
        if (distinct) {
            template.append(" DISTINCT");
        }
        return appendColumns(columns);
    }

    private QueryDsl appendColumns(String... columns) {
        if (columns.length > 0) {
            template.append(SPACE).append(columns[0]);
            for (int i = 1; i < columns.length; i++) {
                appendCommaAnd(columns[i]);
            }
        }
        return this;
    }

    private void appendCommaAnd(String value) {
        template.append(COMMA).append(SPACE).append(value);
    }

    @Override
    public QueryDsl selectDistinct(String... columns) {
        return select(true, columns);
    }

    @Override
    public QueryDsl selectAll() {
        template.append("SELECT *");
        return this;
    }

    @Override
    public QueryDsl from(String table) {
        template.append(" FROM ").append(table);
        return this;
    }

    @Override
    public QueryDsl insertInto(String table) {
        template.append("INSERT INTO ").append(table);
        insert = true;
        return this;
    }

    @Override
    public QueryDsl update(String table) {
        template.append("UPDATE ").append(table).append(" SET ");
        update = true;
        return this;
    }

    @Override
    public QueryDsl set(String column, Object value) {
        if (!update) {
            template.append(COMMA).append(SPACE);
        } else {
            update = false;
        }
        template.append(column).append(" = ").append(VALUE_PLACEHOLDER);
        values.add(value);
        return this;
    }

    @Override
    public QueryDsl delete(String table) {
        template.append("DELETE FROM ").append(table);
        return this;
    }

    @Override
    public QueryDsl into(String table) {
        template.append(" INTO ").append(table);
        return this;
    }

    @Override
    public QueryDsl where(String column) {
        template.append(" WHERE ").append(column);
        return this;
    }

    @Override
    public QueryDsl equal() {
        template.append(" =");
        return this;
    }

    @Override
    public QueryDsl notEqual() {
        template.append(" !=");
        return this;
    }

    @Override
    public QueryDsl less() {
        template.append(" <");
        return this;
    }

    @Override
    public QueryDsl lessEqual() {
        template.append(" <=");
        return this;
    }

    @Override
    public QueryDsl greater() {
        template.append(" >");
        return this;
    }

    @Override
    public QueryDsl greaterEqual() {
        template.append(" >=");
        return this;
    }

    @Override
    public QueryDsl between() {
        template.append(" BETWEEN");
        return this;
    }

    @Override
    public QueryDsl in() {
        template.append(" IN");
        return this;
    }

    @Override
    public QueryDsl like(String pattern) {
        template.append(" LIKE ").append(escaped(pattern));
        return this;
    }

    private String escaped(String value) {
        return String.format("'%s'", value);
    }

    @Override
    public QueryDsl isNull() {
        template.append(" IS NULL");
        return this;
    }

    @Override
    public QueryDsl isNotNull() {
        template.append(" IS NOT NULL");
        return this;
    }

    @Override
    public QueryDsl exists() {
        template.append(" EXISTS");
        return this;
    }

    @Override
    public QueryDsl any() {
        template.append(" ANY");
        return this;
    }

    @Override
    public QueryDsl all() {
        template.append(" ALL");
        return this;
    }

    @Override
    public QueryDsl not() {
        template.append(" NOT");
        return this;
    }

    @Override
    public QueryDsl and() {
        template.append(" AND");
        return this;
    }

    @Override
    public QueryDsl or() {
        template.append(" OR");
        return this;
    }

    @Override
    public QueryDsl orderBy(String... columns) {
        template.append(" ORDER BY");
        return appendColumns(columns);
    }

    @Override
    public QueryDsl asc() {
        template.append(" ASC");
        return this;
    }

    @Override
    public QueryDsl desc() {
        template.append(" DESC");
        return this;
    }

    @Override
    public QueryDsl limit(int value) {
        return addValue(" LIMIT ", value);
    }

    private QueryDsl addValue(String prefix, Object value) {
        template.append(prefix).append(VALUE_PLACEHOLDER);
        values.add(value);
        return this;
    }

    @Override
    public QueryDsl offset(int value) {
        return addValue(" OFFSET ", value);
    }

    @Override
    public QueryDsl value(Object value) {
        template.append(SPACE).append(VALUE_PLACEHOLDER);
        values.add(value);
        return this;
    }

    @Override
    public QueryDsl nextValue(Object value) {
        template.append(COMMA);
        return value(value);
    }

    @Override
    public QueryDsl values(Object value, Object... values) {
        if (insert) {
            template.append(" VALUES");
            insert = false;
        }
        template.append(BRACKET_START).append(VALUE_PLACEHOLDER);
        this.values.add(value);
        for (Object v : values) {
            appendCommaAnd(VALUE_PLACEHOLDER);
            this.values.add(v);
        }
        template.append(BRACKET_END);
        return this;
    }

    @Override
    public QueryDsl column(String column) {
        template.append(" ").append(column);
        return this;
    }

    @Override
    public QueryDsl nextColumn(String column) {
        appendCommaAnd(column);
        return this;
    }

    @Override
    public QueryDsl columns(String column, String... columns) {
        template.append(BRACKET_START).append(column);
        for (String c : columns) {
            appendCommaAnd(c);
        }
        template.append(BRACKET_END);
        return this;
    }

    private QueryDsl inBracket(String value) {
        template.append(BRACKET_START).append(value).append(BRACKET_END);
        return this;
    }

    @Override
    public QueryDsl innerJoin(String table) {
        return join("INNER", table);
    }

    private QueryDsl join(String type, String table) {
        template.append(SPACE).append(type).append(" JOIN ").append(table);
        return this;
    }

    @Override
    public QueryDsl leftJoin(String table) {
        return join("LEFT", table);
    }

    @Override
    public QueryDsl rightJoin(String table) {
        return join("RIGHT", table);
    }

    @Override
    public QueryDsl fullJoin(String table) {
        return join("FULL", table);
    }

    @Override
    public QueryDsl crossJoin(String table) {
        return join("CROSS", table);
    }

    @Override
    public QueryDsl union() {
        template.append(" UNION ");
        return this;
    }

    @Override
    public QueryDsl unionAll() {
        template.append(" UNION ALL ");
        return this;
    }

    @Override
    public QueryDsl on(String firstColumn, String secondColumn) {
        template.append(" ON ").append(firstColumn).append(" = ").append(secondColumn);
        return this;
    }

    @Override
    public QueryDsl on(String column) {
        template.append(" ON ").append(column);
        return this;
    }

    @Override
    public QueryDsl count(String column) {
        template.append(" COUNT");
        return inBracket(column);
    }

    @Override
    public QueryDsl avg(String column) {
        template.append(" AVG");
        return inBracket(column);
    }

    @Override
    public QueryDsl sum(String column) {
        template.append(" SUM");
        return inBracket(column);
    }

    @Override
    public QueryDsl groupBy(String... columns) {
        template.append(" GROUP BY");
        return appendColumns(columns);
    }

    @Override
    public QueryDsl having() {
        template.append(" HAVING");
        return this;
    }

    @Override
    public QueryDsl as(String alias) {
        template.append(" AS ").append(alias);
        return this;
    }

    @Override
    public QueryDsl openBracket() {
        template.append(BRACKET_START);
        return this;
    }

    @Override
    public QueryDsl closeBracket() {
        template.append(BRACKET_END);
        return this;
    }

    @Override
    public Query query() {
        return query;
    }

    public String template() {
        return template.toString();
    }
}
