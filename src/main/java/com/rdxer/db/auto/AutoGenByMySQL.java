package com.rdxer.db.auto;

import com.rdxer.db.auto.model.FieldMeta;
import com.rdxer.db.auto.model.TableMeta;
import com.rdxer.db.auto.utils.DbFieldTypeUtils;
import com.rdxer.db.auto.utils.StringEx;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoGenByMySQL extends BaseAutoGen {

    @Override
    protected void creteField(JdbcTemplate jdbcTemplate, TableMeta tableMeta, FieldMeta fieldMeta) {
        StringBuilder sql = new StringBuilder();
        StringBuilder otherSql = new StringBuilder();


        String fieldName = fieldMeta.getName();

        // 列名
        sql.append("alter table %s add column ".formatted(tableMeta.getName()));
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
            // AUTO_INCREMENT
            if (fieldMeta.isIncrement()) {
                sql.append("AUTO_INCREMENT");
                sql.append(" ");
            }
            // 是否可为空
            if (!fieldMeta.isIsnull()) {
                sql.append("not null");
                sql.append(" ");
            }
            // 是否 唯一性
            if (fieldMeta.isUnique()) {
                otherSql.append("ALTER TABLE %s ADD UNIQUE (%s);".formatted(tableMeta.getName(), fieldName));
            }
            // 拼接注释
            if (StringEx.hasText(fieldMeta.getComment())) {
                sql.append("comment '%s'".formatted(tableMeta.getComment()));
                sql.append(" ");
            }
        }

        sql.append(";");
        // 注释
        System.out.println(sql);
        jdbcTemplate.execute(sql.toString());
        System.out.println(otherSql);
        if (!otherSql.isEmpty()) {
            jdbcTemplate.execute(otherSql.toString());
        }
    }

    @Override
    protected List<String> getAllFieldByTable(JdbcTemplate jdbcTemplate, TableMeta tableMeta) {
        String fieldSQL = """
                select COLUMN_NAME as name
                from information_schema.COLUMNS
                where table_name =  '%s' and TABLE_SCHEMA =  (%s);
                """.formatted(tableMeta.getName(), "select database()");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(fieldSQL);
        return list.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    }

    @Override
    protected void creteTable(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, TableMeta tableMeta) {
        StringBuilder sql = new StringBuilder();
        StringBuilder otherSql = new StringBuilder();

        // 1. 名
        sql.append("""
                    create table %s
                (
                    """.formatted(tableMeta.getName()));

        // 2. 字段
        for (FieldMeta fieldMeta : tableMeta.getFieldList()) {
            String fieldName = fieldMeta.getName();

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
            // AUTO_INCREMENT
            if (fieldMeta.isIncrement()) {
                sql.append("AUTO_INCREMENT");
                sql.append(" ");
            }
            // 是否可为空
            if (!fieldMeta.isIsnull()) {
                sql.append("not null");
                sql.append(" ");
            }
            // 是否 唯一性
            if (fieldMeta.isUnique()) {
                otherSql.append("ALTER TABLE %s ADD UNIQUE (%s);".formatted(tableMeta.getName(), fieldName));
            }
            // 拼接注释
            if (StringEx.hasText(fieldMeta.getComment())) {
                sql.append("comment '%s'".formatted(tableMeta.getComment()));
                sql.append(" ");
            }
            sql.append(",");
        }
        // 去除 ，
        sql.replace(sql.length() - 1, sql.length(), "");
        sql.append(") ");

        if (StringEx.hasText(tableMeta.getComment())) {
            sql.append(" comment '%s'".formatted(tableMeta.getComment()));
        }

        sql.append(" engine = InnoDB;");
        sql.append("\n");

        // 注释
        System.out.println(sql);
        jdbcTemplate.execute(sql.toString());
        System.out.println(otherSql);
        if (!otherSql.isEmpty()) {
            jdbcTemplate.execute(otherSql.toString());
        }
    }

    @Override
    protected void preprocesField(FieldMeta fieldMeta) {
        DbFieldTypeUtils.preprocesFieldByMySql(fieldMeta);
    }

    @Override
    protected List<String> getAllTableNameList(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {

        String findAllTableSQL = """
                    show tables;
                """;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(findAllTableSQL);

        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        if (list.get(0).keySet().size() == 0) {
            return new ArrayList<>();
        }
        String key = list.get(0).keySet().stream().toList().get(0);

        return list.stream().map(v -> v.get(key).toString()).filter(StringUtils::hasText).collect(Collectors.toList());
    }
}
