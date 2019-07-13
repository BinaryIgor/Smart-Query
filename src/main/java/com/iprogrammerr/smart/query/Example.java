package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .select("ProductName").from("Product")
            .where("Id").in()
            .openBracket()
            .select("ProductId").from("OrderItem")
            .where("Quantity").greater().value(100)
            .closeBracket()
            .query();
        System.out.println(query.template());
        System.out.println(query.values());
    }
}
