package com.iprogrammerr.smart.query;

import java.util.List;

public class SmartQueryDsl implements QueryDsl {

    private static final String VALUE_PLACEHOLDER = "?";
    private static final String COMMA = ",";
    private static final String SPACE = " ";
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
    public QueryDsl value(Object value) {
        builder.append(" ").append(VALUE_PLACEHOLDER);
        values.add(value);
        return this;
    }

    @Override
    public QueryDsl column(String column) {
        builder.append(" ").append(column);
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
