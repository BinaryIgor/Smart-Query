package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Date;

@SuppressWarnings("unchecked")
public class ClassMapping<T> implements ResultMapping<T> {

    private final Class<T> clazz;

    public ClassMapping(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T value(ResultSet result) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        Class<?>[] ctrTypes = new Class<?>[fields.length];
        Object[] values = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            ctrTypes[i] = f.getType();
            int idx = fieldIndex(f, result);
            if (idx >= 0) {
                values[i] = fieldValue(f.getType(), idx, result);
            }
        }
        Constructor<T> constructor = clazz.getConstructor(ctrTypes);
        if (constructor == null) {
            throw new RuntimeException(String.format("Cant't find appropriate constructor for declared fields: %s",
                Arrays.toString(ctrTypes)));
        }
        return constructor.newInstance(values);
    }

    private int fieldIndex(Field field, ResultSet result) throws Exception {
        Annotation a = field.getAnnotation(Mapping.class);
        int idx = -1;
        if (a == null) {
            idx = result.findColumn(field.getName());
        } else {
            Mapping m = (Mapping) a;
            String[] values = m.keys();
            for (String v : values) {
                idx = result.findColumn(v);
                if (idx >= 0) {
                    break;
                }
            }
            if (idx < 0) {
                idx = result.findColumn(field.getName());
            }
        }
        return idx;
    }

    private Object fieldValue(Class<?> clazz, int idx, ResultSet result) throws Exception {
        Object value;
        if (Number.class.isAssignableFrom(clazz)) {
            value = numberValue((Class<? extends Number>) clazz, idx, result);
        } else {
            value = value(clazz, idx, result);
        }
        return value;
    }

    private Number numberValue(Class<? extends Number> clazz, int idx, ResultSet result) throws Exception {
        Number value;
        if (clazz.equals(Double.class)) {
            value = result.getDouble(idx);
        } else if (clazz.equals(Float.class)) {
            value = result.getFloat(idx);
        } else if (clazz.equals(Long.class)) {
            value = result.getLong(idx);
        } else if (clazz.equals(Integer.class)) {
            value = result.getInt(idx);
        } else if (clazz.equals(Short.class)) {
            value = result.getShort(idx);
        } else {
            value = result.getByte(idx);
        }
        if (result.wasNull()) {
            value = null;
        }
        return value;
    }

    private Object value(Class<?> clazz, int idx, ResultSet result) throws Exception {
        Object value;
        if (clazz.equals(String.class)) {
            value = result.getString(idx);
        } else if (clazz.equals(Boolean.class)) {
            value = result.getBoolean(idx);
        } else if (clazz.equals(byte[].class) || clazz.equals(Byte[].class)) {
            value = result.getBytes(idx);
        } else if (clazz.equals(Date.class)) {
            value = result.getDate(idx);
        } else {
            value = result.getObject(idx);
        }
        return value;
    }

}
