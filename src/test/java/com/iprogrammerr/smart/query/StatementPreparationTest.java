package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        Mockito.doAnswer(a -> {
            argsCapture.put(a.getArgument(0), a.getArgument(1));
            return true;
        }).when(statement);

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

    private interface StatementMockPreparation {
        void prepare(PreparedStatement mock) throws Exception;
    }
}
