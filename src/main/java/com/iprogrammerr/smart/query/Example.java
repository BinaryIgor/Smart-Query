package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .insertInto("author")
            .columns("name", "alias")
            .values("Adam", "Stasiek")
            .build();
        System.out.println(query.template());
    }
}
