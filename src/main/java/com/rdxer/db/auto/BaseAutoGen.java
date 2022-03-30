package com.rdxer.db.auto;

import com.rdxer.db.auto.annotation.AutoColumn;
import com.rdxer.db.auto.model.FieldMeta;
import com.rdxer.db.auto.model.TableMeta;
import com.rdxer.db.auto.utils.NameEx;
import com.rdxer.db.auto.utils.StringEx;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

public abstract class BaseAutoGen {

    public void autoGen(List<TableMeta> tableMetas, List<Class<?>> needAutoClass, JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        List<String> tableNameList = getAllTableNameList(jdbcTemplate, transactionTemplate);
//        System.out.println("---");
//        tableNameList.forEach(System.out::println);

        // 预处理
        for (TableMeta tableMeta : tableMetas) {
            preprocess(tableMeta);
        }

        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                for (TableMeta tableMeta : tableMetas) {
                    // 1. 判断是否存在 不存在则创建表
                    String tableName = tableMeta.getName();
                    if (!tableNameList.contains(tableName)) {
                        // 不存在 - 建表
                        creteTable(jdbcTemplate, transactionTemplate, tableMeta);
                        // 创建完表格
                        continue;
                    }
                    // 2. 存在则判断字段
                    // 2.1 获取指定表的所有字段
                    List<String> fields = getAllFieldByTable(jdbcTemplate, tableMeta);

                    for (FieldMeta fieldMeta : tableMeta.getFieldList()) {
                        String clazzName = fieldMeta.getName();
                        if (fields.contains(clazzName)) {
                            // 已经存在跳过
                            continue;
                        }
                        // 不存在则创建
                        creteField(jdbcTemplate, tableMeta, fieldMeta);
                    }
                }
                return null;
            }
        });
    }

    protected abstract void creteField(JdbcTemplate jdbcTemplate, TableMeta tableMeta, FieldMeta fieldMeta);

    protected abstract List<String> getAllFieldByTable(JdbcTemplate jdbcTemplate, TableMeta tableMeta);

    protected abstract void creteTable(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, TableMeta tableMeta);

    protected void preprocess(TableMeta tableMeta) {
        if (StringEx.isBalnk(tableMeta.getName())) {
            tableMeta.setName(makeTableName(tableMeta.getClazz()));
        }
//        System.out.println(" >>>>>>>> " + tableMeta.getName());
        for (FieldMeta fieldMeta : tableMeta.getFieldList()) {
            AutoColumn anno = fieldMeta.getColumn();

            if (anno != null) {
                fieldMeta.setName(anno.name());
                fieldMeta.setComment(anno.comment());
                fieldMeta.setDefine(anno.define());
                fieldMeta.setIncrement(anno.isAutoIncrement());
                fieldMeta.setIsnull(anno.isNull());
                fieldMeta.setKey(anno.isKey());
                fieldMeta.setUnique(anno.isUnique());
                fieldMeta.setLen(anno.len());
            }
            if (StringEx.isBalnk(fieldMeta.getName())) {
                String name = NameEx.camelToUnderline(fieldMeta.getField().getName());
                fieldMeta.setName(name);
            }

            preprocesField(fieldMeta);

        }
    }

    protected abstract void preprocesField(FieldMeta fieldMeta);

    private String makeTableName(Class<?> clazz) {
        String name = NameEx.camelToUnderline(clazz.getSimpleName());
        return name;
    }

    protected abstract List<String> getAllTableNameList(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate);


}
