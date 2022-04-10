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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoGenByMySQL extends BaseAutoGen {
    Logger logger = LoggerFactory.getLogger(AutoGenByMySQL.class);

    @Override
    protected void creteField(JdbcTemplate jdbcTemplate, TableMeta tableMeta, FieldMeta fieldMeta) {
        StringBuilder sql = new StringBuilder();
        StringBuilder otherSql = new StringBuilder();


        String fieldName = fieldMeta.getName();

        // 列名
        sql.append(String.format("alter table %s add column ", tableMeta.getName()));
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
                otherSql.append(String.format("ALTER TABLE %s ADD UNIQUE (%s);", tableMeta.getName(), fieldName));
            }
            // 拼接注释
            if (StringEx.hasText(fieldMeta.getComment())) {
                sql.append(String.format("comment '%s'", tableMeta.getComment()));
                sql.append(" ");
            }
        }

        sql.append(";");
        // 注释
        logger.info(sql.toString());
        jdbcTemplate.execute(sql.toString());
        logger.info(otherSql.toString());
        if (StringEx.hasText(otherSql)) {
            jdbcTemplate.execute(otherSql.toString());
        }
    }

    @Override
    protected List<String> getAllFieldByTable(JdbcTemplate jdbcTemplate, TableMeta tableMeta) {
        String fieldSQL = "select COLUMN_NAME as name\n" +
                "from information_schema.COLUMNS\n" +
                "where table_name =  '%s' and TABLE_SCHEMA =  (%s);";
        fieldSQL = String.format(fieldSQL, tableMeta.getName(), "select database()");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(fieldSQL);
        return list.stream().map(v -> v.get("name").toString()).collect(Collectors.toList());
    }

    @Override
    protected void creteTable(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate, TableMeta tableMeta) {
        StringBuilder sql = new StringBuilder();
        List<String> otherSqlList = new ArrayList<>();

        // 1. 名
        sql.append(String.format("  create table %s ( ", tableMeta.getName()));

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
                sql.append(String.format("(%s)", fieldMeta.getLen()));
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
                otherSqlList.add(String.format("ALTER TABLE %s ADD UNIQUE (%s);\n", tableMeta.getName(), fieldName));
            }
            // 拼接注释
            if (StringEx.hasText(fieldMeta.getComment())) {
                sql.append(String.format("comment '%s'", tableMeta.getComment()));
                sql.append(" ");
            }
            sql.append(",");
        }
        // 去除 ，
        sql.replace(sql.length() - 1, sql.length(), "");
        sql.append(") ");

        if (StringEx.hasText(tableMeta.getComment())) {
            sql.append(String.format(" comment '%s'", tableMeta.getComment()));
        }

        sql.append(" engine = InnoDB;");
        sql.append("\n");

        // 建表
        logger.info(sql.toString());
        jdbcTemplate.execute(sql.toString());

        // field
        logger.info(String.join("",otherSqlList));
        for (String s : otherSqlList) {
            if (StringEx.hasText(s)) {
                jdbcTemplate.execute(s);
            }
        }
    }

    @Override
    protected void preprocesField(FieldMeta fieldMeta) {
        DbFieldTypeUtils.preprocesFieldByMySql(fieldMeta);
    }

    @Override
    protected List<String> getAllTableNameList(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {

        String findAllTableSQL = "show tables;";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(findAllTableSQL);

        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        if (list.get(0).keySet().size() == 0) {
            return new ArrayList<>();
        }

        String key = list.get(0).keySet().iterator().next();

        return list.stream().map(v -> v.get(key).toString()).filter(StringUtils::hasText).collect(Collectors.toList());
    }
}
