package com.iprogrammerr.smart.query.example.active;

import com.iprogrammerr.smart.query.QueryFactory;
import com.iprogrammerr.smart.query.active.ActiveRecord;
import com.iprogrammerr.smart.query.active.UpdateableColumn;
import com.iprogrammerr.smart.query.example.table.Organism;

public class OrganismRecord extends ActiveRecord<String, Organism> {

    public OrganismRecord(QueryFactory factory, String dna) {
        super(factory, Organism.TABLE, new UpdateableColumn<>(Organism.DNA, dna), String.class, false);
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

    public OrganismRecord setDna(String dna) {
        set(Organism.DNA, dna);
        return this;
    }
}
