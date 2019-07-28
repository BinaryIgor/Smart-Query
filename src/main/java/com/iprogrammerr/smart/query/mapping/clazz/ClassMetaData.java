package com.iprogrammerr.smart.query.mapping.clazz;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class ClassMetaData<T> {

    private final Map<Field, Type> fieldsTypes;
    private final Class<T> clazz;
    private final Semaphore mutex;
    private Constructor<T> constructor;

    public ClassMetaData(Class<T> clazz) {
        this.clazz = clazz;
        this.fieldsTypes = new LinkedHashMap<>();
        this.mutex = new Semaphore(1);
    }

    public void init() {
        try {
            mutex.acquire();
            scanFields();
            findConstructor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    private void findConstructor() {
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

    private void scanFields() {
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            Embedded e = f.getAnnotation(Embedded.class);
            if (e == null) {
                fieldsTypes.put(f, Type.PRIMITIVE);
            } else {
                fieldsTypes.put(f, Type.OBJECT);
            }
        }
    }

    public Constructor<T> constructor() {
        if (constructor == null) {
            throw new RuntimeException("not initialized, call init() first!");
        }
        return constructor;
    }

    public Map<Field, Type> fieldsWithTypes() {
        try {
            mutex.acquire();
            return fieldsTypes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
    }

    public enum Type {
        PRIMITIVE, OBJECT;

        public boolean isPrimitive() {
            return this == PRIMITIVE;
        }
    }
}
