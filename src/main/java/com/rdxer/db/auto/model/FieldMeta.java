package com.rdxer.db.auto.model;

import com.rdxer.db.auto.annotation.AutoColumn;

import java.lang.reflect.Field;

public class FieldMeta {
    private AutoColumn column;
    private Field field;

    private String name;
    private String define;
    private String len;
    private String comment;
    private boolean increment = false;
    private boolean key = false;
    private boolean isnull = true;
    private boolean unique = false;
    private String dbtype;

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

    public boolean isIsnull() {
        return isnull;
    }

    public void setIsnull(boolean isnull) {
        this.isnull = isnull;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }


    public AutoColumn getColumn() {
        return column;
    }

    public void setColumn(AutoColumn column) {
        this.column = column;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
    }

    public String getDbtype() {
        return dbtype;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
