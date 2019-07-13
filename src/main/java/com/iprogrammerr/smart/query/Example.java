package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .select().column("a").as("A").nextColumn("b").as("B")
            .from("author").as("A")
            .where("A").in()
            .openBracket()
            .value(1).nextValue(2).nextValue(3)
            .closeBracket()
            .query();
        System.out.println(query.template());
        System.out.println(query.values());
    }
}
