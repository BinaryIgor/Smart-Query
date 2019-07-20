package com.iprogrammerr.smart.query.active;

import com.iprogrammerr.smart.query.Query;
import com.iprogrammerr.smart.query.QueryDsl;
import com.iprogrammerr.smart.query.QueryFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class ActiveRecord<T> {

    protected final QueryFactory factory;
    protected final String table;
    protected final UpdateableColumn id;
    protected final UpdateableColumn[] columns;

    protected ActiveRecord(QueryFactory factory, String table, UpdateableColumn id, UpdateableColumn... columns) {
        this.factory = factory;
        this.table = table;
        this.id = id;
        this.columns = columns;
    }

    protected void set(String column, Object value) {
        if (id.name().equals(column)) {
            id.setValue(value);
        } else {
            for (UpdateableColumn c : columns) {
                if (c.name().equals(column)) {
                    c.setValue(value);
                    break;
                }
            }
        }
    }

    public abstract T fetch();

    protected Query fetchQuery() {
        return factory.newQuery().dsl()
            .selectAll().from(table).where(id.name()).equal().value(id.value())
            .query();
    }

    public void insert() {
        insert(false);
    }

    public long insertReturningId() {
        return insert(true);
    }

    private long insert(boolean returnId) {
        List<UpdateableColumn> changed = changed();
        long id = -1;
        if (!changed.isEmpty()) {
            Query query = insertQuery(changed);
            if (returnId) {
                id = query.executeReturningId();
                setId(id);
            } else {
                query.execute();
            }
        }
        return id;
    }

    private Query insertQuery(List<UpdateableColumn> changed) {
        QueryDsl dsl = factory.newQuery().dsl().insertInto(table);
        String[] columns = new String[changed.size() - 1];
        Object[] values = new Object[changed.size() - 1];
        for (int i = 1; i < changed.size(); i++) {
            UpdateableColumn value = changed.get(i);
            columns[i - 1] = value.name();
            values[i - 1] = value.value();
        }
        UpdateableColumn first = changed.get(0);
        return dsl.columns(first.name(), columns).values(first.value(), values).query();
    }

    private void setId(long id) {
        try {
            UpdateableColumn<Number> castedId = (UpdateableColumn<Number>) this.id;
            castedId.setValue(id);
        } catch (Exception e) {
            throw new RuntimeException("There was a problem while using auto generated id ", e);
        }
    }

    private List<UpdateableColumn> changed() {
        List<UpdateableColumn> changed = new ArrayList<>();
        for (UpdateableColumn c : columns) {
            if (c.isUpdated()) {
                changed.add(c);
            }
        }
        return changed;
    }

    public void update() {
        List<UpdateableColumn> changed = changed();
        QueryDsl dsl = factory.newQuery().dsl().update(table);
        for (UpdateableColumn c : changed) {
            dsl.set(c.name(), c.value());
        }
        dsl.where(id.name()).equal().value(id.value())
            .query()
            .execute();
    }

    public void delete() {
        factory.newQuery().dsl().delete(table).where(id.name()).equal().value(id.value())
            .query()
            .execute();
    }
}
