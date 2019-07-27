package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.SmartQueryFactory;
import com.iprogrammerr.smart.query.TestDatabase;
import com.iprogrammerr.smart.query.example.Organism;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ManyMappingTest {

    private QueryFactory factory;

    @Before
    public void setup() {
        TestDatabase setup = new TestDatabase();
        setup.setup();
        factory = new SmartQueryFactory(setup.source());
    }

    @Test
    public void doesMapping() {
        Organism first = new Organism("AT-CG");
        Organism second = new Organism("ATATAT");
        Organism third = new Organism("CG-ATAT-CG");

        factory.newQuery().dsl()
            .insertInto(Organism.TABLE).values(first.dna)
            .query().end().dsl()
            .insertInto(Organism.TABLE).values(second.dna)
            .query().end().dsl()
            .insertInto(Organism.TABLE).values(third.dna)
            .query()
            .executeTransaction();

        List<Organism> actual = factory.newQuery().dsl()
            .select(Organism.DNA).as("id").from(Organism.TABLE)
            .query()
            .fetch(Mapping.listOfClass(Organism.class));

        MatcherAssert.assertThat(actual, Matchers.contains(first, second, third));
    }
}
