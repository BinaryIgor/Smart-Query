package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .select().count("*").from("author")
            .query();
        System.out.println(query.template());
        System.out.println(query.values());
    }
}
