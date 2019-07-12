package com.iprogrammerr.smart.query;

public interface DialectTranslation {

    DialectTranslation DEFAULT = sql -> sql;

    String translated(String sql);
}
