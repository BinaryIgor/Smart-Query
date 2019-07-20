package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;

public class OrganismRecord extends ActiveRecord<String, Organism> {

    public OrganismRecord(QueryFactory factory, String dna) {
        super(factory, Organism.TABLE, false, String.class,
            new UpdateableColumn<>(Organism.DNA, dna));
    }

    public OrganismRecord(QueryFactory factory) {
        this(factory, null);
    }

    @Override
    public Organism fetch() {
        return fetchQuery().fetch(r -> {
            r.next();
            return new Organism(r);
        });
    }

    public void setDna(String dna) {
        set(Organism.DNA, dna);
    }
}
