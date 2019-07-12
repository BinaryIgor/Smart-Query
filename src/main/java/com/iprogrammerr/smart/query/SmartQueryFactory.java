package com.iprogrammerr.smart.query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.Callable;

public class SmartQueryFactory implements QueryFactory {

    private final Callable<Connection> source;
    private final DialectTranslation translation;
    private final boolean closeConnections;

    public SmartQueryFactory(Callable<Connection> source, DialectTranslation translation, boolean closeConnections) {
        this.source = source;
        this.translation = translation;
        this.closeConnections = closeConnections;
    }

    public SmartQueryFactory(Callable<Connection> source, boolean closeConnections) {
        this(source, DialectTranslation.DEFAULT, closeConnections);
    }

    public SmartQueryFactory(Callable<Connection> source) {
        this(source, true);
    }

    public SmartQueryFactory(DataSource source, DialectTranslation translation, boolean closeConnections) {
        this(source::getConnection, translation, closeConnections);
    }

    public SmartQueryFactory(DataSource source, boolean closeConnections) {
        this(source, DialectTranslation.DEFAULT, closeConnections);
    }

    public SmartQueryFactory(DataSource source) {
        this(source, true);
    }

    @Override
    public Query newQuery() {
        try {
            return new SmartQuery(source.call(), translation, closeConnections);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
