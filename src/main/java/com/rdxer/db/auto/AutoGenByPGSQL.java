package com.rdxer.db.auto;

import com.rdxer.db.auto.model.FieldMeta;
import com.rdxer.db.auto.model.TableMeta;
import com.rdxer.db.auto.utils.DbFieldTypeUtils;
import com.rdxer.db.auto.utils.StringEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoGenByPGSQL extends BaseAutoGen {

    Logger logger = LoggerFactory.getLogger(AutoGenByPGSQL.class);

    private String findAllTableSQL = "select tablename from pg_tables where schemaname = 'public';";

    @Override
    protected void creteField(JdbcTemplate jdbcTemplate, TableMeta tableMeta, FieldMeta fieldMeta) {
        StringBuilder commentsSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        sql.append(String.format("alter table %s add column ", tableMeta.getName()));

        String fieldName = fieldMeta.getName();
        // 拼接注释
        if (StringEx.hasText(fieldMeta.getComment())) {
            commentsSql.append(String.format("comment on COLUMN %s.%s is '%s';\n", tableMeta.getName(), fieldName, fieldMeta.getComment()));
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
                sql.append(String.format("(%s)", fieldMeta.getLen()));
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
                sql.append(String.format("constraint uk_%s_%s unique", tableMeta.getName(), fieldName));
                sql.append(" ");
            }
        }

        sql.append(";");
        sql.append(commentsSql);
        logger.info(sql.toString());
        jdbcTemplate.execute(sql.toString());
    }

    @Override
    protected List<String> getAllFieldByTable(JdbcTemplate jdbcTemplate, TableMeta tableMeta) {
        String fieldSQL = "SELECT\n" +
                "                    col_description (A .attrelid, A .attnum) AS COMMENT,\n" +
                "                    format_type (A .atttypid, A .atttypmod) AS TYPE,\n" +
                "                    A .attname AS NAME,\n" +
                "                    A .attnotnull AS nullable\n" +
                "                FROM\n" +
                "                    pg_class AS C,\n" +
                "                    pg_attribute AS A\n" +
                "                WHERE\n" +
                "                        C .relname = '%s' --指定表\n" +
                "                  AND A .attrelid = C .oid\n" +
                "                  AND A .attnum > 0;";
        fieldSQL = String.format(fieldSQL, tableMeta.getName());

        List<Map<String, Object>> list = jdbcTemplate.queryForList(fieldSQL);
        List<String> names = list.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
        return names;
    }

    @Override
    protected void creteTable(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, TableMeta tableMeta) {
        StringBuilder sql = new StringBuilder();
        StringBuilder commentsSql = new StringBuilder();

        // 1. 名
        sql.append(String.format(" create table %s \n(", tableMeta.getName()));

        if (StringEx.hasText(tableMeta.getComment())) {
            commentsSql.append(String.format("comment on table %s is '%s';\n", tableMeta.getName(), tableMeta.getComment()));
        }
        // 2. 字段
        for (FieldMeta fieldMeta : tableMeta.getFieldList()) {
            String fieldName = fieldMeta.getName();
            // 拼接注释
            if (StringEx.hasText(fieldMeta.getComment())) {
                commentsSql.append(String.format("comment on COLUMN %s.%s is '%s';\n", tableMeta.getName(), fieldName, fieldMeta.getComment()));
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
                sql.append(String.format("(%s)", fieldMeta.getLen()));
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
                sql.append(String.format("constraint uk_%s_%s unique", tableMeta.getName(), fieldName));
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
        logger.info(sql.toString());
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
