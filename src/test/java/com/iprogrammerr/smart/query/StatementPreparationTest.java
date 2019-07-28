package com.iprogrammerr.smart.query;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashMap;
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
        setsNumbers(Double.class, 4.5, 5.6);
    }

    private <T extends Number> void setsNumbers(Class<T> clazz, T first, T second) {
        Map<Integer, T> argsCapture = new HashMap<>();
        Mockito.doAnswer(a -> {
            argsCapture.put(a.getArgument(0), a.getArgument(1));
            return true;
        }).when(statement);

        prepareNumbersStatement(clazz, first, second);

        MatcherAssert.assertThat(argsCapture, Matchers.hasEntry(1, first));
        MatcherAssert.assertThat(argsCapture, Matchers.hasEntry(2, second));
    }

    private <T extends Number> void prepareNumbersStatement(Class<T> clazz, T first, T second) {
        try {
            if (Types.isDouble(clazz)) {
                statement.setDouble(Mockito.anyInt(), Mockito.anyDouble());
            } else if (Types.isFloat(clazz)) {
                statement.setFloat(Mockito.anyInt(), Mockito.anyFloat());
            } else if (Types.isLong(clazz)) {
                statement.setLong(Mockito.anyInt(), Mockito.anyLong());
            } else if (Types.isInt(clazz)) {
                statement.setInt(Mockito.anyInt(), Mockito.anyInt());
            } else if (Types.isShort(clazz)) {
                statement.setShort(Mockito.anyInt(), Mockito.anyShort());
            } else {
                statement.setByte(Mockito.anyInt(), Mockito.anyByte());
            }

            String sql = "INSERT INTO measures(?, ?)";
            prepareStatement(sql);
            preparation.prepare(sql, Arrays.asList(first, second));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareStatement(String sql) throws Exception {
        Mockito.when(connection.prepareStatement(Mockito.eq(sql), Mockito.anyInt(), Mockito.anyInt(),
            Mockito.anyInt())).thenReturn(statement);
    }

    @Test
    public void setsFloats() {
        setsNumbers(Float.class, 4.4f, -8.4f);
    }

    @Test
    public void setsLongs() {
        setsNumbers(Long.class, 2L, 4L);
    }

    @Test
    public void setsInts() {
        setsNumbers(Integer.class, 1, -33);
    }

    @Test
    public void setsShorts() {
        setsNumbers(Short.class, (short) 3, (short) 4);
    }

    @Test
    public void setsBytes() {
        setsNumbers(Byte.class, (byte) 22, (byte) -33);
    }
}
