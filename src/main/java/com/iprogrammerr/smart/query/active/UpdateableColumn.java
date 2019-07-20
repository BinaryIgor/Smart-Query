package com.iprogrammerr.smart.query.active;

public class UpdateableColumn<T> {

    private final String name;
    private T value;
    private boolean updated;

    public UpdateableColumn(String name, T value) {
        this.name = name;
        this.value = value;
        this.updated = false;
    }

    public UpdateableColumn(String name) {
        this(name, null);
    }

    public void setValue(T value) {
        this.value = value;
        this.updated = true;
    }

    public String name() {
        return name;
    }

    public T value() {
        return value;
    }

    public boolean isUpdated() {
        return updated;
    }
}
