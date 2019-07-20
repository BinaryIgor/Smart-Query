package com.iprogrammerr.smart.query.example;

import java.sql.ResultSet;

public class Organism {

    public static final String TABLE = "organism";
    public static final String DNA = "dna";

    public final String dna;

    public Organism(String dna) {
        this.dna = dna;
    }

    public Organism(ResultSet result) throws Exception {
        this(result.getString(DNA));
    }
}
