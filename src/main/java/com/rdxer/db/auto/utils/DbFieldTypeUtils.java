package com.rdxer.db.auto.utils;

import com.rdxer.db.auto.model.FieldMeta;

public class DbFieldTypeUtils {

    public static void preprocesFieldByPGSql(FieldMeta fieldMeta) {

        // 如果设置有 直接略过
        if (StringEx.hasText(fieldMeta.getDefine())) {
            return;
        }

        Class<?> fieldType = fieldMeta.getField().getType();
        String len = fieldMeta.getLen();
        fieldMeta.setLen("");
        // int
        if (fieldType == int.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("integer");

            if (fieldMeta.isIncrement()) {
                fieldMeta.setDbtype("serial");
            }
        } else if (fieldType == Integer.class) {
            fieldMeta.setDbtype("integer");

            if (fieldMeta.isIncrement()) {
                fieldMeta.setDbtype("serial");
            }
        }
        // long
        else if (fieldType == long.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("bigint");

            if (fieldMeta.isIncrement()) {
                fieldMeta.setDbtype("bigserial");
            }
        } else if (fieldType == Long.class) {
            fieldMeta.setDbtype("bigint");

            if (fieldMeta.isIncrement()) {
                fieldMeta.setDbtype("bigserial");
            }
        }
        // float
        else if (fieldType == float.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("real");
        } else if (fieldType == Float.class) {
            fieldMeta.setDbtype("real");
        }
        // double
        else if (fieldType == double.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("double precision");
        } else if (fieldType == Double.class) {
            fieldMeta.setDbtype("double precision");
        }
        // bool
        else if (fieldType == boolean.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("boolean");
        } else if (fieldType == Boolean.class) {

            fieldMeta.setDbtype("boolean");
        }
        // char
        else if (fieldType == char.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("char");
        } else if (fieldType == Character.class) {
            fieldMeta.setDbtype("char");
        }
        // string
        else if (fieldType == String.class) {
            fieldMeta.setDbtype("varchar");
            if (StringEx.hasText(len)) {
                fieldMeta.setLen(len);
            } else {
                fieldMeta.setLen("255");
            }
        } else {
            throw new RuntimeException(String.format("不支持此字段~ %s : %s", fieldMeta.getName(), fieldMeta.getField().getName()));
        }
    }

    public static void preprocesFieldByMySql(FieldMeta fieldMeta) {
        // 如果设置有 直接略过
        if (StringEx.hasText(fieldMeta.getDefine())) {
            return;
        }
        Class<?> fieldType = fieldMeta.getField().getType();
        String len = fieldMeta.getLen();
        fieldMeta.setLen("");
        // int
        if (fieldType == int.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("integer");
        } else if (fieldType == Integer.class) {
            fieldMeta.setDbtype("integer");
        }
        // long
        else if (fieldType == long.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("bigint");
        } else if (fieldType == Long.class) {
            fieldMeta.setDbtype("bigint");
        }
        // float
        else if (fieldType == float.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("float");
        } else if (fieldType == Float.class) {
            fieldMeta.setDbtype("float");
        }
        // double
        else if (fieldType == double.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("double");
        } else if (fieldType == Double.class) {
            fieldMeta.setDbtype("double");
        }
        // bool
        else if (fieldType == boolean.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("bit");
        } else if (fieldType == Boolean.class) {
            fieldMeta.setDbtype("bit");
        }
        // char
        else if (fieldType == char.class) {
            fieldMeta.setIsnull(false);
            fieldMeta.setDbtype("char");
        } else if (fieldType == Character.class) {
            fieldMeta.setDbtype("char");
        }
        // string
        else if (fieldType == String.class) {
            fieldMeta.setDbtype("varchar");
            if (StringEx.hasText(len)) {
                fieldMeta.setLen(len);
            } else {
                fieldMeta.setLen("255");
            }
        } else {
            throw new RuntimeException(String.format("不支持此字段~ %s : %s", fieldMeta.getName(), fieldMeta.getField().getName()));
        }
    }
}
