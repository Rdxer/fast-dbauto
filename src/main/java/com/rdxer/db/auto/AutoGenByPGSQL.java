package com.rdxer.db.auto;

import com.rdxer.db.auto.model.FieldMeta;
import com.rdxer.db.auto.model.TableMeta;
import com.rdxer.db.auto.utils.DbFieldTypeUtils;
import com.rdxer.db.auto.utils.StringEx;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoGenByPGSQL extends BaseAutoGen {


    private String findAllTableSQL = """
            select tablename from pg_tables where schemaname = 'public';
                        """;

    @Override
    protected void creteField(JdbcTemplate jdbcTemplate, TableMeta tableMeta, FieldMeta fieldMeta) {
        StringBuilder commentsSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        sql.append("alter table %s add column ".formatted(tableMeta.getName()));

        String fieldName = fieldMeta.getName();
        // 拼接注释
        if (StringEx.hasText(fieldMeta.getComment())) {
            commentsSql.append("comment on COLUMN %s.%s is '%s';\n".formatted(tableMeta.getName(), fieldName, fieldMeta.getComment()));
        }
        // 列名
        sql.append(fieldName);
        sql.append(" ");
        // 是否是全自定义
        String define = fieldMeta.getDefine();
        if (StringEx.hasText(define)) {
            sql.append(define);
        } else {
            // 拼接推断的数据类型
            sql.append(fieldMeta.getDbtype());
            if (StringEx.hasText(fieldMeta.getLen())) {
                sql.append("(%s)".formatted(fieldMeta.getLen()));
            }
            sql.append(" ");
            // 主键
            if (fieldMeta.isKey()) {
                sql.append("primary key");
                sql.append(" ");
            }
            // 是否可为空
            if (fieldMeta.isIsnull() == false) {
                sql.append("not null");
                sql.append(" ");
            }
            // 是否 唯一性
            if (fieldMeta.isUnique()) {
                sql.append("constraint uk_%s_%s unique".formatted(tableMeta.getName(), fieldName));
                sql.append(" ");
            }
        }

        sql.append(";");
        sql.append(commentsSql);
        System.out.println(sql);
        jdbcTemplate.execute(sql.toString());
    }

    @Override
    protected List<String> getAllFieldByTable(JdbcTemplate jdbcTemplate, TableMeta tableMeta) {
        String fieldSQL = """
                SELECT
                    col_description (A .attrelid, A .attnum) AS COMMENT,
                    format_type (A .atttypid, A .atttypmod) AS TYPE,
                    A .attname AS NAME,
                    A .attnotnull AS nullable
                FROM
                    pg_class AS C,
                    pg_attribute AS A
                WHERE
                        C .relname = '%s' --指定表
                  AND A .attrelid = C .oid
                  AND A .attnum > 0;
                """.formatted(tableMeta.getName());

        List<Map<String, Object>> list = jdbcTemplate.queryForList(fieldSQL);
        List<String> names = list.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
        return names;
    }

    @Override
    protected void creteTable(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, TableMeta tableMeta) {
        StringBuilder sql = new StringBuilder();
        StringBuilder commentsSql = new StringBuilder();

        // 1. 名
        sql.append("""
                    create table %s
                (
                    """.formatted(tableMeta.getName()));
        if (StringEx.hasText(tableMeta.getComment())) {
            commentsSql.append("comment on table %s is '%s';\n".formatted(tableMeta.getName(), tableMeta.getComment()));
        }
        // 2. 字段
        for (FieldMeta fieldMeta : tableMeta.getFieldList()) {
            String fieldName = fieldMeta.getName();
            // 拼接注释
            if (StringEx.hasText(fieldMeta.getComment())) {
                commentsSql.append("comment on COLUMN %s.%s is '%s';\n".formatted(tableMeta.getName(), fieldName, fieldMeta.getComment()));
            }
            // 列名
            sql.append(fieldName);
            sql.append(" ");
            // 是否是全自定义
            String define = fieldMeta.getDefine();
            if (StringEx.hasText(define)) {
                sql.append(define);
                sql.append(",");
                continue;
            }
            // 拼接推断的数据类型
            sql.append(fieldMeta.getDbtype());
            if (StringEx.hasText(fieldMeta.getLen())) {
                sql.append("(%s)".formatted(fieldMeta.getLen()));
            }
            sql.append(" ");
            // 主键
            if (fieldMeta.isKey()) {
                sql.append("primary key");
                sql.append(" ");
            }
            // 是否可为空
            if (fieldMeta.isIsnull() == false) {
                sql.append("not null");
                sql.append(" ");
            }
            // 是否 唯一性
            if (fieldMeta.isUnique()) {
                sql.append("constraint uk_%s_%s unique".formatted(tableMeta.getName(), fieldName));
                sql.append(" ");
            }

            sql.append(",");
        }
        // 去除 ，
        sql.replace(sql.length() - 1, sql.length(), "");
        sql.append(");");
        sql.append("\n");

        // 注释
        sql.append(commentsSql);
        System.out.println(sql);
        jdbcTemplate.execute(sql.toString());
    }

    @Override
    protected void preprocesField(FieldMeta fieldMeta) {
        DbFieldTypeUtils.preprocesFieldByPGSql(fieldMeta);
    }

    @Override
    protected List<String> getAllTableNameList(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(findAllTableSQL);
        List<String> tablename = list.stream().map(v -> v.get("tablename").toString()).filter(v -> StringUtils.hasText(v)).collect(Collectors.toList());
        return tablename;
    }
}
