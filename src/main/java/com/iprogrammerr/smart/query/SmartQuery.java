package com.iprogrammerr.smart.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SmartQuery implements Query {

    private static final String TEMPLATES_SEPARATOR = ";";
    private final Connection connection;
    private final StringBuilder template;
    private final List<Object> values;
    private final DialectTranslation translation;
    private final boolean close;

    public SmartQuery(Connection connection, DialectTranslation translation, boolean close) {
        this.connection = connection;
        this.template = new StringBuilder();
        this.values = new ArrayList<>();
        this.translation = translation;
        this.close = close;
    }

    public SmartQuery(Connection connection, boolean close) {
        this(connection, DialectTranslation.DEFAULT, close);
    }

    public SmartQuery(Connection connection, DialectTranslation translation) {
        this(connection, translation, true);
    }

    public SmartQuery(Connection connection) {
        this(connection, DialectTranslation.DEFAULT);
    }

    @Override
    public Query sql(String sql) {
        if (shouldAppendSpace()) {
            template.append(" ");
        }
        template.append(sql);
        return this;
    }

    private boolean shouldAppendSpace() {
        int length = template.length();
        boolean append;
        if (length > 0) {
            char last = template.charAt(length - 1);
            append = !Character.isSpaceChar(last) && last != TEMPLATES_SEPARATOR.charAt(0);
        } else {
            append = false;
        }
        return append;
    }

    @Override
    public Query end() {
        if (template.length() > 0) {
            template.append(TEMPLATES_SEPARATOR);
        }
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
    public QueryDsl dsl() {
        return new SmartQueryDsl(this, template, values);
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
            if (close) {
                connection.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement prepared(boolean returnId) throws Exception {
        PreparedStatement ps;
        String query = template();
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
    public void executeTransaction() {
        boolean autoCommit = true;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            executeStatements();
            connection.commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeTransaction(autoCommit);
        }
    }

    private void closeTransaction(boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
            connection.rollback();
            closeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeStatements() throws Exception {
        List<PreparedStatement> ts = transactionStatements();
        try {
            for (PreparedStatement s : ts) {
                s.executeUpdate();
            }
        } finally {
            for (PreparedStatement s : ts) {
                s.close();
            }
        }
    }

    private List<PreparedStatement> transactionStatements() throws Exception {
        List<PreparedStatement> transactions = new ArrayList<>();
        String[] templates = template().split(TEMPLATES_SEPARATOR);
        int valuesStart = 0;
        for (String t : templates) {
            long params = t.chars().filter(ch -> ch == '?').count();
            PreparedStatement ps = connection.prepareStatement(t);
            for (int i = 0; i < params; i++) {
                ps.setObject(i + 1, values.get(valuesStart + i));
            }
            transactions.add(ps);
            valuesStart += params;
        }
        return transactions;
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
        return translation.translated(template.toString());
    }

    public List<Object> values() {
        return values;
    }
}
