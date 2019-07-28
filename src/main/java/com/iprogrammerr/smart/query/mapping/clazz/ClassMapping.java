package com.iprogrammerr.smart.query.mapping.clazz;

import com.iprogrammerr.smart.query.ResultMapping;
import com.iprogrammerr.smart.query.Types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassMapping<T> implements ResultMapping<T> {

    private static final Map<String, ClassMetaData<?>> CLASSES_META_DATA = new ConcurrentHashMap<>();
    private final Class<T> clazz;
    private final boolean moveResult;

    public ClassMapping(Class<T> clazz, boolean moveResult) {
        this.clazz = clazz;
        this.moveResult = moveResult;
    }

    public ClassMapping(Class<T> clazz) {
        this(clazz, false);
    }

    @Override
    public T value(ResultSet result) throws Exception {
        if (moveResult) {
            result.next();
        }
        ClassMetaData<T> metaData = metaData();
        Map<String, Integer> labelsIndices = labelsIndices(result.getMetaData());
        Object[] values = new Object[metaData.fieldsWithTypes().size()];
        int i = 0;
        for (Map.Entry<Field, ClassMetaData.Type> e : metaData.fieldsWithTypes().entrySet()) {
            Field f = e.getKey();
            if (e.getValue().isPrimitive()) {
                int idx = fieldIndex(f, labelsIndices);
                if (idx >= 0) {
                    values[i] = fieldValue(f.getType(), idx, result);
                }
            } else {
                values[i] = new ClassMapping<>(f.getType()).value(result);
            }
            i++;
        }
        return newInstance(metaData, values);
    }

    private T newInstance(ClassMetaData<T> metaData, Object[] values) {
        Constructor<T> constructor = metaData.constructor();
        try {
            return constructor.newInstance(values);
        } catch (Exception e) {
            throw new RuntimeException(failToCreateMessage(constructor, values), e);
        }
    }

    private String failToCreateMessage(Constructor<T> constructor, Object[] values) {
        return new StringBuilder()
            .append("Constructor:")
            .append(System.lineSeparator())
            .append(constructor)
            .append(System.lineSeparator())
            .append("Values:")
            .append(System.lineSeparator())
            .append(Arrays.toString(values))
            .toString();
    }

    private Map<String, Integer> labelsIndices(ResultSetMetaData meta) throws Exception {
        Map<String, Integer> labelsIndices = new HashMap<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            labelsIndices.put(meta.getColumnLabel(i).toLowerCase(), i);
        }
        return labelsIndices;
    }

    @SuppressWarnings("unchecked")
    private ClassMetaData<T> metaData() {
        String key = clazz.getName();
        if (CLASSES_META_DATA.containsKey(key)) {
            return (ClassMetaData<T>) CLASSES_META_DATA.get(key);
        }
        ClassMetaData<T> metaData = new ClassMetaData<>(clazz);
        CLASSES_META_DATA.put(key, metaData);
        metaData.init();
        return metaData;
    }

    private int fieldIndex(Field field, Map<String, Integer> labelsIndices) {
        int idx = -1;
        Mapping mapping = field.getAnnotation(Mapping.class);
        if (mapping != null) {
            for (String l : mapping.value()) {
                idx = labelsIndices.getOrDefault(l.toLowerCase(), -1);
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
        if (Types.isNumberOrPrimitive(clazz)) {
            value = numberOrPrimitive(clazz, idx, result);
        } else {
            value = objectValue(clazz, idx, result);
        }
        return value;
    }

    private Object numberOrPrimitive(Class<?> clazz, int idx, ResultSet result) throws Exception {
        Object value;
        if (Types.isDouble(clazz)) {
            value = result.getDouble(idx);
        } else if (Types.isFloat(clazz)) {
            value = result.getFloat(idx);
        } else if (Types.isLong(clazz)) {
            value = result.getLong(idx);
        } else if (Types.isInt(clazz)) {
            value = result.getInt(idx);
        } else if (Types.isShort(clazz)) {
            value = result.getShort(idx);
        } else if (Types.isByte(clazz)) {
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
        if (Types.isString(clazz)) {
            value = result.getString(idx);
        } else if (Types.isPrimitiveBytes(clazz)) {
            value = result.getBytes(idx);
        } else if (Types.isSqlDate(clazz)) {
            value = result.getDate(idx);
        } else if (Types.isTime(clazz)) {
            value = result.getTime(idx);
        } else if (Types.isTimestamp(clazz)) {
            value = result.getTimestamp(idx);
        } else if (Types.isBlob(clazz)) {
            value = result.getBlob(idx);
        } else {
            value = result.getObject(idx);
        }
        return value;
    }
}
