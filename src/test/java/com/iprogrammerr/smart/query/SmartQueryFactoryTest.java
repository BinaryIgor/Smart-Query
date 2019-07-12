package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SmartQueryFactoryTest {

    @Test
    public void createsSmartQuery() {
        MatcherAssert.assertThat(new SmartQueryFactory().newQuery().getClass(), Matchers.equalTo(SmartQuery.class));
    }
}
