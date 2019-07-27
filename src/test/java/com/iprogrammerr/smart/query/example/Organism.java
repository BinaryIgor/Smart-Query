package com.iprogrammerr.smart.query.example;

import com.iprogrammerr.smart.query.mapping.clazz.Mapping;

import java.sql.ResultSet;
import java.util.Objects;

public class Organism {

    public static final String TABLE = "organism";
    public static final String DNA = "dna";

    @Mapping(labels = "id")
    public final String dna;

    public Organism(String dna) {
        this.dna = dna;
    }

    public Organism(ResultSet result) throws Exception {
        this(result.getString(DNA));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organism organism = (Organism) o;
        return Objects.equals(dna, organism.dna);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dna);
    }
}
