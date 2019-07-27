package com.iprogrammerr.smart.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SmartQuery implements Query {

    private static final String TEMPLATES_SEPARATOR = ";";
    private static final char VALUES_PLACEHOLDER = '?';
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
            append = !(Character.isSpaceChar(last) || last == TEMPLATES_SEPARATOR.charAt(0));
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
            return mapping.value(ps.executeQuery());
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
            ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY,
                ResultSet.CLOSE_CURSORS_AT_COMMIT);
        }
        for (int i = 0; i < values.size(); i++) {
            setValue(ps, i, values.get(i));
        }
        return ps;
    }

    private void setValue(PreparedStatement prepared, int index, Object value) throws Exception {
        prepared.setObject(index + 1, value);
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
        boolean rollback = true;
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            executeStatements();
            connection.commit();
            rollback = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeTransaction(autoCommit, rollback);
        }
    }

    private void closeTransaction(boolean autoCommit, boolean rollback) {
        try {
            if (rollback) {
                connection.rollback();
            }
            connection.setAutoCommit(autoCommit);
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
            long params = t.chars().filter(ch -> ch == VALUES_PLACEHOLDER).count();
            PreparedStatement ps = connection.prepareStatement(t);
            for (int i = 0; i < params; i++) {
                setValue(ps, i, values.get(valuesStart + i));
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
