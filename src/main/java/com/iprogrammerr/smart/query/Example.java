package com.iprogrammerr.smart.query;

import java.sql.Connection;

public class Example {

    public static void main(String... args) {
        Connection connection = null;
        SmartQuery query = new SmartQuery(connection);
        query.dsl()
            .selectAll().from("day").as("d").innerJoin("meal as m").on("d.id", "m.day_id")
            .innerJoin("meal_product as mp").on("m.id", "mp.meal_id")
            .innerJoin("product as p").on("mp.product_id", "p.id")
            .innerJoin("food as f").on("p.food_id", "f.id");
        //Generated query template
        System.out.println(query.template());
        //TO bind values
        System.out.println(query.values());
    }
}
