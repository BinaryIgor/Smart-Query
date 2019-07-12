package com.iprogrammerr.smart.query;

import java.util.ArrayList;
import java.util.List;

public class SmartQuery implements Query {

    private final StringBuilder template;
    private final List<Object> values;

    public SmartQuery() {
        this.template = new StringBuilder();
        this.values = new ArrayList<>();
    }

    @Override
    public Query sql(String sql) {
        if (template.length() > 0) {
            template.append(" ");
        }
        template.append(sql);
        return this;
    }

    @Override
    public Query set(Object value, Object... values) {
        this.values.add(value);
        for (Object v : values) {
            this.values.add(v);
        }
        return this;
    }

    public String template() {
        return template.toString();
    }

    public List<Object> values() {
        return values;
    }
}
