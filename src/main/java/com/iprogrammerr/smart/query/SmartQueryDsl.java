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

    public SmartQueryDsl(Query query, StringBuilder template, List<Object> values) {
        this.query = query;
        this.template = template;
        this.values = values;
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
    public QueryDsl value(Object value) {
        template.append(SPACE).append(VALUE_PLACEHOLDER);
        values.add(value);
        return this;
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
    public QueryDsl columns(String column, String... columns) {
        template.append(BRACKET_START).append(column);
        for (String c : columns) {
            appendCommaAnd(c);
        }
        template.append(BRACKET_END);
        return this;
    }

    @Override
    public QueryDsl subquery(String subquery) {
        template.append(SPACE).append(BRACKET_START).append(subquery).append(BRACKET_END);
        return this;
    }

    @Override
    public Query build() {
        return query;
    }

    public String template() {
        return template.toString();
    }
}
