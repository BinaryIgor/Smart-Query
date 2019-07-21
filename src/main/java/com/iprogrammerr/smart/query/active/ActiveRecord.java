package com.iprogrammerr.smart.query.active;

import com.iprogrammerr.smart.query.Query;
import com.iprogrammerr.smart.query.QueryDsl;
import com.iprogrammerr.smart.query.QueryFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class ActiveRecord<Id, Table> {

    protected final QueryFactory factory;
    protected final String table;
    protected final UpdateableColumn<Id> id;
    protected final Class<Id> idClazz;
    protected final boolean autoIncrementId;
    protected final UpdateableColumn[] columns;

    public ActiveRecord(QueryFactory factory, String table, UpdateableColumn<Id> id, Class<Id> idClazz,
        boolean autoIncrementId, UpdateableColumn... columns) {
        this.factory = factory;
        this.table = table;
        this.id = id;
        this.idClazz = idClazz;
        this.autoIncrementId = autoIncrementId;
        this.columns = columns;
    }

    public abstract Table fetch();

    protected void set(String column, Object value) {
        if (shouldSetId(column, value)) {
            id.setNewValue(idClazz.cast(value));
        } else {
            for (UpdateableColumn c : columns) {
                if (c.name().equals(column)) {
                    c.setNewValue(value);
                    break;
                }
            }
        }
    }

    private boolean shouldSetId(String column, Object value) {
        return !autoIncrementId && column.equals(id.name()) && (value == null
            || value.getClass().isAssignableFrom(idClazz));
    }

    public Id getId() {
        Id value = id.value();
        if (value == null) {
            throw new RuntimeException("Id isn't initialized");
        }
        return value;
    }

    private void setId(long id) {
        try {
            if (idClazz.isAssignableFrom(Long.class)) {
                this.id.setNewValue(idClazz.cast(id));
            } else if (idClazz.isAssignableFrom(Integer.class)) {
                this.id.setNewValue(idClazz.cast((int) id));
            } else {
                this.id.setNewValue(idClazz.cast((short) id));
            }
        } catch (Exception e) {
            throw new RuntimeException("Autoincrement id must be either long, int or short", e);
        }
    }

    protected Query fetchQuery() {
        return factory.newQuery().dsl()
            .selectAll().from(table).where(id.name()).equal().value(getId())
            .query();
    }

    public void insert() {
        List<UpdateableColumn> changed = changed();
        if (shouldInsertWithAutoIncrement(changed)) {
            Query query = insertQuery(changed);
            long id = query.executeReturningId();
            setId(id);
            markChange(changed);
        } else if (shouldInsertWithNaturalId(changed)) {
            insertQuery(changed).execute();
            markChange(changed);
        }
    }

    private boolean shouldInsertWithAutoIncrement(List<UpdateableColumn> changed) {
        return autoIncrementId && !changed.isEmpty();
    }

    private boolean shouldInsertWithNaturalId(List<UpdateableColumn> changed) {
        return !autoIncrementId && (!changed.isEmpty() || id.isUpdated());
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

    private List<UpdateableColumn> changed() {
        List<UpdateableColumn> changed = new ArrayList<>();
        if (!autoIncrementId && id.isUpdated()) {
            changed.add(id);
        }
        for (UpdateableColumn c : columns) {
            if (c.isUpdated()) {
                changed.add(c);
            }
        }
        return changed;
    }

    public void update() {
        List<UpdateableColumn> changed = changed();
        if (!changed.isEmpty()) {
            QueryDsl dsl = factory.newQuery().dsl().update(table);
            for (UpdateableColumn c : changed) {
                dsl.set(c.name(), c.value());
            }
            dsl.where(id.name()).equal().value(getId())
                .query()
                .execute();
            markChange(changed);
        }
    }

    private void markChange(List<UpdateableColumn> updated) {
        for (UpdateableColumn c : updated) {
            c.setOldValue();
        }
    }

    public void delete() {
        factory.newQuery().dsl().delete(table).where(id.name()).equal().value(getId())
            .query()
            .execute();
    }

    public int unsavedChanges() {
        return changed().size();
    }
}
