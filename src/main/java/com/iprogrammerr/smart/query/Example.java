package com.iprogrammerr.smart.query;

import java.net.Authenticator;
import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .insertInto("user").columns("name", "surname", "email")
            .values("Alan", "Turing", "alan_turing@email.com")
            .query().end()
            .dsl()
            .delete("user").where("name").notEqual().value("Alan")
            .query().end()
            .dsl()
            .update("user").set("name", "Machine").where("name").equal().value("Alan")
            .query();
        //Generated query template
        System.out.println(query.template());
        //To bind values
        System.out.println(query.values());
    }
}
