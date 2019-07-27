package com.iprogrammerr.smart.query;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

public class StatementPreparation {

    private final Connection connection;

    public StatementPreparation(Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement prepare(String query, List<Object> values, boolean returnId) throws Exception {
        PreparedStatement ps;
        if (returnId) {
            ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        } else {
            ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY,
                ResultSet.CLOSE_CURSORS_AT_COMMIT);
        }
        for (int i = 0; i < values.size(); i++) {
            setValue(ps, jdbcIndex(i), values.get(i));
        }
        return ps;
    }

    private int jdbcIndex(int index) {
        return index + 1;
    }

    private void setValue(PreparedStatement prepared, int index, Object value) throws Exception {
        if (Types.isNumberOrPrimitive(value.getClass())) {
            setNumberOrPrimitive(prepared, index, value);
        } else {
            setObject(prepared, index, value);
        }
    }

    private void setNumberOrPrimitive(PreparedStatement prepared, int index, Object value) throws Exception {
        Class<?> clazz = value.getClass();
        if (Types.isDouble(clazz)) {
            setValue(prepared, index, (double) value);
        } else if (Types.isFloat(clazz)) {
            setValue(prepared, index, (float) value);
        } else if (Types.isLong(clazz)) {
            setValue(prepared, index, (long) value);
        } else if (Types.isInt(clazz)) {
            setValue(prepared, index, (int) value);
        } else if (Types.isShort(clazz)) {
            setValue(prepared, index, (short) value);
        } else if (Types.isByte(clazz)) {
            setValue(prepared, index, (byte) value);
        } else if (Types.isBoolean(clazz)) {
            setValue(prepared, index, (boolean) value);
        } else {
            prepared.setObject(index, value);
        }
    }

    private void setObject(PreparedStatement prepared, int index, Object value) throws Exception {
        Class<?> clazz = value.getClass();
        if (Types.isString(clazz)) {
            setValue(prepared, index, (String) value);
        } else if (Types.isBytes(clazz)) {
            setValue(prepared, index, (byte[]) value);
        } else if (Types.isSqlDate(clazz)) {
            setValue(prepared, index, (Date) value);
        } else if (Types.isTime(clazz)) {
            setValue(prepared, index, (Time) value);
        } else if (Types.isTimestamp(clazz)) {
            setValue(prepared, index, (Timestamp) value);
        } else if (Types.isBlob(clazz)) {
            setValue(prepared, index, (Blob) value);
        } else {
            prepared.setObject(index, value);
        }
    }

    private void setValue(PreparedStatement prepared, int index, double value) throws Exception {
        prepared.setDouble(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, float value) throws Exception {
        prepared.setFloat(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, long value) throws Exception {
        prepared.setLong(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, int value) throws Exception {
        prepared.setInt(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, short value) throws Exception {
        prepared.setShort(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, byte value) throws Exception {
        prepared.setByte(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, boolean value) throws Exception {
        prepared.setBoolean(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, String value) throws Exception {
        prepared.setString(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, byte[] value) throws Exception {
        prepared.setBytes(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, Date value) throws Exception {
        prepared.setDate(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, Time value) throws Exception {
        prepared.setTime(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, Timestamp value) throws Exception {
        prepared.setTimestamp(index, value);
    }

    private void setValue(PreparedStatement prepared, int index, Blob value) throws Exception {
        prepared.setBlob(index, value);
    }
}
