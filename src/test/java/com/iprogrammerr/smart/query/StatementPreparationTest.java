package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatementPreparationTest {

    private Connection connection;
    private StatementPreparation preparation;
    private PreparedStatement statement;

    @Before
    public void setup() {
        connection = Mockito.mock(Connection.class);
        statement = Mockito.mock(PreparedStatement.class);
        preparation = new StatementPreparation(connection);
    }

    @Test
    public void setsDoubles() {
        setsValues("INSERT INTO measures(?, ?)", m -> m.setDouble(Mockito.anyInt(), Mockito.anyDouble()),
            4.5, 5.6);
    }

    private <T> void setsValues(String sql, StatementMockPreparation mockPreparation, T value, T... values) {
        List<Object> allValues = new ArrayList<>();
        allValues.add(value);
        allValues.addAll(Arrays.asList(values));

        Map<Integer, T> argsCapture = new HashMap<>();
        captureArgs(argsCapture);

        try {
            mockPreparation.prepare(statement);
            prepareStatement(sql, allValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < allValues.size(); i++) {
            MatcherAssert.assertThat(argsCapture, Matchers.hasEntry(i + 1, allValues.get(i)));
        }
    }

    private void captureArgs(Map<Integer, ?> argsCapture) {
        Mockito.doAnswer(a -> {
            argsCapture.put(a.getArgument(0), a.getArgument(1));
            return true;
        }).when(statement);
    }

    private void prepareStatement(String sql, List<Object> values) {
        try {
            Mockito.when(connection.prepareStatement(Mockito.eq(sql), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(statement);
            preparation.prepare(sql, values);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void setsFloats() {
        setsValues("INSERT INTO floats(?, ?, ?)", m -> m.setFloat(Mockito.anyInt(), Mockito.anyFloat()),
            4.4f, -8.4f, 44f);
    }

    @Test
    public void setsLongs() {
        setsValues("SELECT * FROM longs WHERE longest = ? and shortest = ?",
            m -> m.setLong(Mockito.anyInt(), Mockito.anyLong()),
            4L, -4L);
    }

    @Test
    public void setsInts() {
        setsValues("SELECT * FROM ints WHERE min > ? and max < ?", m -> m.setInt(Mockito.anyInt(), Mockito.anyInt()),
            -33, 44);
    }

    @Test
    public void setsShorts() {
        setsValues("INSERT INTO shorts(?, ?)", m -> m.setShort(Mockito.anyInt(), Mockito.anyShort()),
            (short) 3, (short) 4);
    }

    @Test
    public void setsBytes() {
        setsValues("INSERT INTO bytes(?, ?, ?)", m -> m.setByte(Mockito.anyInt(), Mockito.anyByte()),
            (byte) 1, (byte) 33, (byte) 44);
    }

    @Test
    public void setsBooleans() {
        setsValues("UPDATE booleans set true = ?, false = ?", m -> m.setBoolean(Mockito.anyInt(), Mockito.anyBoolean()),
            false, true);
    }

    @Test
    public void setsStrings() {
        setsValues("INSERT INTO strings(?, ?, ?, ?)", m -> m.setString(Mockito.anyInt(), Mockito.anyString()),
            "a", "aa", "bb", "b");
    }

    @Test
    public void setsPrimitiveBytesArray() {
        setsValues("INSERT INTO blobs(?, ?)", m -> m.setBytes(Mockito.anyInt(), Mockito.any(byte[].class)),
            new byte[0], new byte[]{1, 2, 3});
    }

    @Test
    public void setsBytesArray() {
        setsValues("SELECT * FROM blobs WHERE id = ?", m -> m.setObject(Mockito.anyInt(), Mockito.any(Byte[].class)),
            new Byte[]{1, 2, 3});
    }

    @Test
    public void setsSqlDates() {
        setsValues("SELECT * FROM dates where d > ? nad d < ?",
            m -> m.setDate(Mockito.anyInt(), Mockito.any(java.sql.Date.class)),
            new java.sql.Date(System.currentTimeMillis()),
            new java.sql.Date(System.currentTimeMillis() - 100));
    }

    @Test
    public void setsTimes() {
        setsValues("SELECT * FROM times where t > ? nad t < ?",
            m -> m.setTime(Mockito.anyInt(), Mockito.any(Time.class)),
            new Time(System.currentTimeMillis()),
            new Time(System.currentTimeMillis() + 100));
    }

    @Test
    public void setsTimestamps() {
        setsValues("UPDATE timestamps set t1 = ?, t2 = ?",
            m -> m.setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class)),
            new Timestamp(System.currentTimeMillis()),
            new Timestamp(System.currentTimeMillis() + 200));
    }

    @Test
    public void setsBlob() throws Exception {
        setsValues("INSERT INTO blob ?", m -> m.setBlob(Mockito.anyInt(), Mockito.any(Blob.class)),
            new SerialBlob(new byte[]{1, 3, 4}));
    }

    @Test
    public void setsVariousTypes() throws Exception {
        Map<Integer, Object> argsCapture = new HashMap<>();

        captureArgs(argsCapture);
        statement.setObject(Mockito.eq(1), Mockito.any());
        captureArgs(argsCapture);
        statement.setDouble(Mockito.eq(2), Mockito.anyDouble());
        captureArgs(argsCapture);
        statement.setString(Mockito.eq(3), Mockito.anyString());
        captureArgs(argsCapture);
        statement.setBoolean(Mockito.eq(4), Mockito.anyBoolean());

        List<Object> values = Arrays.asList(new int[]{4, 3, -2}, 5.44, "Test", true);
        prepareStatement("INSERT INTO various (?, ?, ?, ?)", values);

        for (int i = 0; i < values.size(); i++) {
            MatcherAssert.assertThat(argsCapture, Matchers.hasEntry(i + 1, values.get(i)));
        }
    }

    private interface StatementMockPreparation {
        void prepare(PreparedStatement mock) throws Exception;
    }
}
