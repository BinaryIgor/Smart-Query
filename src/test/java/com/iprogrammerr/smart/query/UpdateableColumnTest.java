package com.iprogrammerr.smart.query;

import com.iprogrammerr.smart.query.active.UpdateableColumn;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class UpdateableColumnTest {

    @Test
    public void changesState() {
        UpdateableColumn<String> column = new UpdateableColumn<>("a", "a");
        MatcherAssert.assertThat(column.isUpdated(), Matchers.equalTo(false));
        column.set("b");
        MatcherAssert.assertThat(column.isUpdated(), Matchers.equalTo(true));
    }
}
