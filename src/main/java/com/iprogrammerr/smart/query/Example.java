package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .select("name").from("author")
            .where("name")
            .not().like("S%")
            .and()
            .column("id").greater().value(1)
            .build();
        System.out.println(query.template());
    }
}
