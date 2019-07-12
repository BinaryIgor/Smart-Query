package com.iprogrammerr.smart.query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.Callable;

public class SmartQueryFactory implements QueryFactory {

    private final Callable<Connection> source;

    public SmartQueryFactory(Callable<Connection> source) {
        this.source = source;
    }

    public SmartQueryFactory(DataSource source) {
        this(source::getConnection);
    }

    @Override
    public Query newQuery() {
        try {
            return new SmartQuery(source.call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
