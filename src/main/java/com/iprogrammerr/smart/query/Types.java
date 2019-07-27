package com.iprogrammerr.smart.query;

import java.sql.Blob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class Types {

    private Types() {
    }

    public static boolean isNumber(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz);
    }

    public static boolean isNumberOrPrimitive(Class<?> clazz) {
        return isNumber(clazz) || clazz.isPrimitive();
    }

    public static boolean isDouble(Class<?> clazz) {
        return clazz.equals(double.class) || clazz.equals(Double.class);
    }

    public static boolean isFloat(Class<?> clazz) {
        return clazz.equals(float.class) || clazz.equals(Float.class);
    }

    public static boolean isLong(Class<?> clazz) {
        return clazz.equals(long.class) || clazz.equals(Long.class);
    }

    public static boolean isInt(Class<?> clazz) {
        return clazz.equals(int.class) || clazz.equals(Integer.class);
    }

    public static boolean isShort(Class<?> clazz) {
        return clazz.equals(short.class) || clazz.equals(Short.class);
    }

    public static boolean isByte(Class<?> clazz) {
        return clazz.equals(byte.class) || clazz.equals(Byte.class);
    }

    public static boolean isBoolean(Class<?> clazz) {
        return clazz.equals(boolean.class) || clazz.equals(Boolean.class);
    }

    public static boolean isString(Class<?> clazz) {
        return clazz.equals(String.class);
    }

    public static boolean isBytes(Class<?> clazz) {
        return clazz.equals(byte[].class) || clazz.equals(Byte[].class);
    }

    public static boolean isDate(Class<?> clazz) {
        return clazz.equals(Date.class);
    }

    public static boolean isSqlDate(Class<?> clazz) {
        return clazz.equals(java.sql.Date.class);
    }

    public static boolean isTime(Class<?> clazz) {
        return clazz.equals(Time.class);
    }

    public static boolean isTimestamp(Class<?> clazz) {
        return clazz.isAssignableFrom(Timestamp.class);
    }

    public static boolean isBlob(Class<?> clazz) {
        return clazz.isAssignableFrom(Blob.class);
    }
}
