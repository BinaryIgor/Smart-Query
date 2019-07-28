package com.iprogrammerr.smart.query.mapping.clazz;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClassMetaData<T> {

    public final Map<Field, Type> fieldsTypes;
    private final Class<T> clazz;
    private Constructor<T> constructor;

    public ClassMetaData(Class<T> clazz) {
        this.clazz = clazz;
        this.fieldsTypes = new LinkedHashMap<>();
    }

    public void putPrimitive(Field field) {
        fieldsTypes.put(field, Type.PRIMITIVE);
    }

    public void putObject(Field field) {
        fieldsTypes.put(field, Type.OBJECT);
    }

    public int countFields() {
        return fieldsTypes.size();
    }

    public Constructor<T> constructor() {
        if (constructor == null) {
            findConstructor();
        }
        return constructor;
    }

    public void findConstructor() {
        Class<?>[] ctrTypes = new Class<?>[fieldsTypes.size()];
        int i = 0;
        for (Field f : fieldsTypes.keySet()) {
            ctrTypes[i++] = f.getType();
        }
        try {
            constructor = clazz.getConstructor(ctrTypes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (constructor == null) {
            throw new RuntimeException(String.format(
                "Cant't find appropriate constructor for declared non-static fields: %s",
                fieldsTypes.keySet()));
        }
    }

    public enum Type {
        PRIMITIVE, OBJECT
    }
}
