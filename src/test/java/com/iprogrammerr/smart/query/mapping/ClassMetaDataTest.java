package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.example.table.Author;
import com.iprogrammerr.smart.query.mapping.clazz.ClassMetaData;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ClassMetaDataTest {

    @Test
    public void notInitializedThrowsException() {
        ClassMetaData<Author> meta = new ClassMetaData<>(Author.class);
        String message = "";
        try {
            meta.constructor();
        } catch (Exception e) {
            message = e.getMessage();
        }
        MatcherAssert.assertThat(message, Matchers.containsString("not initialized, call init() first!"));
    }
}
