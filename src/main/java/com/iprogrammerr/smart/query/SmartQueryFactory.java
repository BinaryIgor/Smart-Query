package com.iprogrammerr.smart.query;

public class SmartQueryFactory implements QueryFactory {

    @Override
    public Query newQuery() {
        return new SmartQuery();
    }
}
