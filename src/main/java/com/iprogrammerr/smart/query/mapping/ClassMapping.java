package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class ClassMapping<T> implements ResultMapping<T> {

    private final Class<T> clazz;

    public ClassMapping(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T value(ResultSet result) throws Exception {
        List<Field> fields = nonStaticFields();
        Class<?>[] ctrTypes = new Class<?>[fields.size()];
        Object[] values = new Object[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
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

    private List<Field> nonStaticFields() {
        List<Field> nonStatic = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                nonStatic.add(f);
            }
        }
        return nonStatic;
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
        if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)) {
            value = numberOrPrimitive(clazz, idx, result);
        } else {
            value = objectValue(clazz, idx, result);
        }
        return value;
    }

    private Object numberOrPrimitive(Class<?> clazz, int idx, ResultSet result) throws Exception {
        Object value;
        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            value = result.getDouble(idx);
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            value = result.getFloat(idx);
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            value = result.getLong(idx);
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            value = result.getInt(idx);
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            value = result.getShort(idx);
        } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            value = result.getByte(idx);
        } else {
            value = result.getBoolean(idx);
        }
        if (result.wasNull()) {
            value = null;
        }
        return value;
    }

    private Object objectValue(Class<?> clazz, int idx, ResultSet result) throws Exception {
        Object value;
        if (clazz.equals(String.class)) {
            value = result.getString(idx);
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
