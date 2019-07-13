package com.iprogrammerr.smart.query;

import java.util.List;

public class SmartQueryDsl implements QueryDsl {

    private static final String VALUE_PLACEHOLDER = "?";
    private static final String COMMA = ",";
    private static final String SPACE = " ";
    private static final String BRACKET_START = "(";
    private static final String BRACKET_END = ")";
    private final Query query;
    private final StringBuilder builder;
    private final List<Object> values;

    public SmartQueryDsl(Query query, StringBuilder builder, List<Object> values) {
        this.query = query;
        this.builder = builder;
        this.values = values;
    }

    @Override
    public QueryDsl select(String... columns) {
        return select(false, columns);
    }

    private QueryDsl select(boolean distinct, String... columns) {
        builder.append("SELECT");
        if (distinct) {
            builder.append(" DISTINCT");
        }
        if (columns.length > 0) {
            builder.append(SPACE).append(columns[0]);
            for (int i = 1; i < columns.length; i++) {
                appendCommaAnd(columns[i]);
            }
        }
        return this;
    }

    private void appendCommaAnd(String value) {
        builder.append(COMMA).append(SPACE).append(value);
    }

    @Override
    public QueryDsl selectDistinct(String... columns) {
        return select(true, columns);
    }

    @Override
    public QueryDsl selectAll() {
        builder.append("SELECT *");
        return this;
    }

    @Override
    public QueryDsl from(String table) {
        builder.append(" FROM ").append(table);
        return this;
    }

    @Override
    public QueryDsl where(String column) {
        builder.append(" WHERE ").append(column);
        return this;
    }

    @Override
    public QueryDsl equal() {
        builder.append(" =");
        return this;
    }

    @Override
    public QueryDsl notEqual() {
        builder.append(" !=");
        return this;
    }

    @Override
    public QueryDsl less() {
        builder.append(" <");
        return this;
    }

    @Override
    public QueryDsl lessEqual() {
        builder.append(" <=");
        return this;
    }

    @Override
    public QueryDsl greater() {
        builder.append(" >");
        return this;
    }

    @Override
    public QueryDsl greaterEqual() {
        builder.append(" >=");
        return this;
    }

    @Override
    public QueryDsl between() {
        builder.append(" BETWEEN");
        return this;
    }

    @Override
    public QueryDsl notBetween() {
        builder.append(" NOT BETWEEN");
        return this;
    }

    @Override
    public QueryDsl in() {
        builder.append(" IN");
        return this;
    }

    @Override
    public QueryDsl notIn() {
        builder.append(" NOT IN");
        return this;
    }

    @Override
    public QueryDsl like(String pattern) {
        builder.append(" LIKE ").append(escaped(pattern));
        return this;
    }

    private String escaped(String value) {
        return String.format("'%s'", value);
    }

    @Override
    public QueryDsl notLike(String pattern) {
        builder.append(" NOT LIKE ").append(escaped(pattern));
        return this;
    }

    @Override
    public QueryDsl value(Object value) {
        builder.append(SPACE).append(VALUE_PLACEHOLDER);
        values.add(value);
        return this;
    }

    @Override
    public QueryDsl values(Object value, Object... values) {
        builder.append(SPACE).append(BRACKET_START).append(VALUE_PLACEHOLDER);
        this.values.add(value);
        for (Object v : values) {
            appendCommaAnd(VALUE_PLACEHOLDER);
            this.values.add(v);
        }
        builder.append(BRACKET_END);
        return this;
    }

    @Override
    public QueryDsl column(String column) {
        builder.append(" ").append(column);
        return this;
    }

    @Override
    public QueryDsl subquery(String subquery) {
        builder.append(SPACE).append(BRACKET_START).append(subquery).append(BRACKET_END);
        return this;
    }

    @Override
    public Query build() {
        return query;
    }

    public String template() {
        return builder.toString();
    }
}
