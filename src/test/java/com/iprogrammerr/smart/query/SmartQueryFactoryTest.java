package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;

public class SmartQueryFactoryTest {

    private final TestDatabaseSetup setup = new TestDatabaseSetup();

    @Test
    public void createsSmartQuery() {
        MatcherAssert.assertThat(new SmartQueryFactory(setup.source()).newQuery().getClass(),
            Matchers.equalTo(SmartQuery.class));
    }

    @Test
    public void configuresSmartQuery() throws Exception {
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

    @Test
    public void createsDefaultSmartQuery() throws Exception {
        setup.setup();
        Connection connection = setup.source().getConnection();

        SmartQueryFactory factory = new SmartQueryFactory(() -> connection);
        SmartQuery query = (SmartQuery) factory.newQuery();
        String sql = "SELECT * FROM author";
        query.sql(sql).fetch(ResultSet::next);

        MatcherAssert.assertThat(connection.isClosed(), Matchers.equalTo(true));
        MatcherAssert.assertThat(query.template(), Matchers.equalTo(sql));
    }
}
