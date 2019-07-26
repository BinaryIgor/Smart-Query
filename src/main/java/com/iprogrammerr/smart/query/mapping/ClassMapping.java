package com.iprogrammerr.smart.query.mapping;

import com.iprogrammerr.smart.query.ResultMapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassMapping<T> implements ResultMapping<T> {

    private final Class<T> clazz;

    public ClassMapping(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T value(ResultSet result) throws Exception {
        List<Field> fields = nonStaticFields();
        Map<String, Integer> labelsIndices = labelsIndices(result.getMetaData());
        Class<?>[] ctrTypes = new Class<?>[fields.size()];
        Object[] values = new Object[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            ctrTypes[i] = f.getType();
            int idx = fieldIndex(f, labelsIndices);
            if (idx >= 0) {
                values[i] = fieldValue(f.getType(), idx, result);
            }
        }
        Constructor<T> constructor = clazz.getConstructor(ctrTypes);
        if (constructor == null) {
            throw new RuntimeException(String.format(
                "Cant't find appropriate constructor for declared non-static fields: %s", fields));
        }
        return constructor.newInstance(values);
    }

    private Map<String, Integer> labelsIndices(ResultSetMetaData meta) throws Exception {
        Map<String, Integer> labelsIndices = new HashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            labelsIndices.put(meta.getColumnLabel(i).toLowerCase(), i);
        }
        return labelsIndices;
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

    private int fieldIndex(Field field, Map<String, Integer> labelsIndices) {
        int idx = -1;
        Mapping mapping = field.getAnnotation(Mapping.class);
        if (mapping != null) {
            for (String k : mapping.keys()) {
                idx = labelsIndices.getOrDefault(k.toLowerCase(), -1);
                if (idx >= 0) {
                    break;
                }
            }
        }
        if (idx < 0) {
            idx = labelsIndices.getOrDefault(field.getName().toLowerCase(), -1);
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
