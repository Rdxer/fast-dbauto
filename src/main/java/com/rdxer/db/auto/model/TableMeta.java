package com.rdxer.db.auto.model;

import java.util.List;


public class TableMeta {
    // 设置的 表名 - 如果为 空 则需要根据类名生成
    private String name;
    // 设置的 注释
    private String comment;
    // 关联的模型类
    private Class<?> clazz;
    // 所有字段 meta
    private List<FieldMeta> fieldList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<FieldMeta> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldMeta> fieldList) {
        this.fieldList = fieldList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
