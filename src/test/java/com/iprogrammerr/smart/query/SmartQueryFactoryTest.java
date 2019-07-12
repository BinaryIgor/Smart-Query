package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SmartQueryFactoryTest {

    @Test
    public void createsSmartQuery() {
        TestDatabaseSetup setup = new TestDatabaseSetup();
        MatcherAssert.assertThat(new SmartQueryFactory(setup.source()).newQuery().getClass(),
            Matchers.equalTo(SmartQuery.class));
    }
}
