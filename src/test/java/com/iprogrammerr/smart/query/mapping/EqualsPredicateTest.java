package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.Author;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class EqualsPredicateTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void returnsEqual() {
        returnsEqualOrNot(true);
    }

    private void returnsEqualOrNot(boolean equal) {
        String author = "Ayn Rand";
        factory.newQuery().dsl()
            .insertInto(Author.TABLE).columns(Author.NAME, Author.ALIAS).values(author, "AR")
            .query()
            .execute();

        boolean result = factory.newQuery().dsl()
            .select(Author.NAME).from(Author.TABLE).where(Author.NAME).equal().value(author)
            .query()
            .fetch(r1 -> {
                r1.next();
                String previous = equal ? author : author + author;
                return new EqualsPredicate<>(r2 -> r2.getString(Author.NAME))
                    .belongsTo(previous, r1);
            });

        MatcherAssert.assertThat(result, Matchers.equalTo(equal));
    }

    @Test
    public void returnsUnequal() {
        returnsEqualOrNot(false);
    }
}
