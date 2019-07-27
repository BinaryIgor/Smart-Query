package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.sql.Blob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TypesTest {

    @Test
    public void isNumberOrPrimitive() {
        List<Class<?>> primitives = Arrays.asList(Number.class, Double.class, double.class, Float.class, float.class,
            Long.class, long.class, Integer.class, int.class, Short.class, short.class, Byte.class, byte.class,
            Boolean.class, boolean.class);
        for (Class<?> p : primitives) {
            isTrue(Types.isNumberOrPrimitive(p));
        }
    }

    private void isTrue(boolean statement) {
        MatcherAssert.assertThat(statement, Matchers.equalTo(true));
    }

    @Test
    public void isDouble() {
        andIsTrue(Types.isDouble(Double.class), Types.isDouble(double.class));
    }

    private void andIsTrue(boolean first, boolean second) {
        isTrue(first && second);
    }

    @Test
    public void isFloat() {
        andIsTrue(Types.isFloat(Float.class), Types.isFloat(float.class));
    }

    @Test
    public void isLong() {
        andIsTrue(Types.isLong(Long.class), Types.isLong(long.class));
    }

    @Test
    public void isInt() {
        andIsTrue(Types.isInt(Integer.class), Types.isInt(int.class));
    }

    @Test
    public void isShort() {
        andIsTrue(Types.isShort(Short.class), Types.isShort(short.class));
    }

    @Test
    public void isByte() {
        andIsTrue(Types.isByte(Byte.class), Types.isByte(byte.class));
    }

    @Test
    public void isBoolean() {
        andIsTrue(Types.isBoolean(Boolean.class), Types.isBoolean(boolean.class));
    }

    @Test
    public void isString() {
        isTrue(Types.isString(String.class));
    }

    @Test
    public void isPrimitiveBytes() {
        isTrue(Types.isPrimitiveBytes(byte[].class));
    }

    @Test
    public void isBytes() {
        andIsTrue(Types.isBytes(Byte[].class), Types.isBytes(byte[].class));
    }

    @Test
    public void isDate() {
        isTrue(Types.isDate(Date.class));
    }

    @Test
    public void isSqlDate() {
        isTrue(Types.isSqlDate(java.sql.Date.class));
    }

    @Test
    public void isTime() {
        isTrue(Types.isTime(Time.class));
    }

    @Test
    public void sTimestamp() {
        isTrue(Types.isTimestamp(Timestamp.class));
    }

    @Test
    public void isBlob() {
        isTrue(Types.isBlob(Blob.class));
    }
}
