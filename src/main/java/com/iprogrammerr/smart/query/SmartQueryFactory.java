package com.iprogrammerr.smart.query;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.Callable;

public class SmartQueryFactory implements QueryFactory {

    private final Callable<Connection> source;
    private DialectTranslation translation;
    private boolean closeConnections;

    public SmartQueryFactory(Callable<Connection> source) {
        this.source = source;
        this.translation = DialectTranslation.DEFAULT;
        this.closeConnections = false;
    }

    public SmartQueryFactory(DataSource source) {
        this(source::getConnection);
    }

    @Override
    public Query newQuery() {
        try {
            return new SmartQuery(source.call(), translation, closeConnections);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setTranslation(DialectTranslation translation) {
        this.translation = translation;
    }

    public void setCloseConnections(boolean closeConnections) {
        this.closeConnections = closeConnections;
    }
}
