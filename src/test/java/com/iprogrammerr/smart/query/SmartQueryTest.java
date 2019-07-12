package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SmartQueryTest {

    @Test
    public void appendsSql() {
        String first = "SELECT * FROM a";
        String second = "WHERE b > 1";
        SmartQuery query = new SmartQuery();
        query.sql(first).sql(second);
        MatcherAssert.assertThat(query.template(), Matchers.equalTo(first + " " + second));
    }

    @Test
    public void bindsValues() {
        int first = 1;
        double second = 4.4;
        String third = "value";
        SmartQuery query = new SmartQuery();
        query.set(first, second, third);
        MatcherAssert.assertThat(query.values(), Matchers.contains(first, second, third));
    }
}
