package com.iprogrammerr.smart.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SmartQuery implements Query {

    private final Connection connection;
    private final StringBuilder template;
    private final List<Object> values;

    public SmartQuery(Connection connection) {
        this.connection = connection;
        this.template = new StringBuilder();
        this.values = new ArrayList<>();
    }

    @Override
    public Query sql(String sql) {
        if (template.length() > 0) {
            template.append(" ");
        }
        template.append(sql);
        return this;
    }

    @Override
    public Query set(Object value, Object... values) {
        this.values.add(value);
        for (Object v : values) {
            this.values.add(v);
        }
        return this;
    }

    @Override
    public <T> T fetch(ResultMapping<T> mapping) {
        try (PreparedStatement ps = prepared()) {
            return mapping.map(ps.executeQuery());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement prepared(boolean returnId) throws Exception {
        PreparedStatement ps;
        String query = template.toString();
        if (returnId) {
            ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        } else {
            ps = connection.prepareStatement(query);
        }
        for (int i = 0; i < values.size(); i++) {
            ps.setObject(i + 1, values.get(i));
        }
        return ps;
    }

    private PreparedStatement prepared() throws Exception {
        return prepared(false);
    }

    @Override
    public void execute() {
        try (PreparedStatement ps = prepared()) {
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    @Override
    public long executeReturningId() {
        try (PreparedStatement ps = prepared(true)) {
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    public String template() {
        return template.toString();
    }

    public List<Object> values() {
        return values;
    }
}
