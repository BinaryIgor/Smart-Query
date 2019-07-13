package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .update("author").set("name", "Stasiek")
            .where("id").equal().value(1)
            .build();
        System.out.println(query.template());
        System.out.println(query.values());
    }
}
