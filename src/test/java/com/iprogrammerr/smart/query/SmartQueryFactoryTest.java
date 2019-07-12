package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.sql.Connection;

public class SmartQueryFactoryTest {

    @Test
    public void createsSmartQuery() {
        TestDatabaseSetup setup = new TestDatabaseSetup();
        MatcherAssert.assertThat(new SmartQueryFactory(setup.source()).newQuery().getClass(),
            Matchers.equalTo(SmartQuery.class));
    }

    @Test
    public void configuresSmartQuery() throws Exception {
        TestDatabaseSetup setup = new TestDatabaseSetup();
        setup.setup();
        Connection connection = setup.source().getConnection();

        String suffix = " WHERE id = 1";

        SmartQueryFactory factory = new SmartQueryFactory(() -> connection, sql -> sql + suffix,
            false);

        SmartQuery query = (SmartQuery) factory.newQuery();
        query.sql("DELETE FROM author").execute();

        MatcherAssert.assertThat(connection.isClosed(), Matchers.equalTo(false));
        MatcherAssert.assertThat(query.template(), Matchers.endsWith(suffix));
    }
}
