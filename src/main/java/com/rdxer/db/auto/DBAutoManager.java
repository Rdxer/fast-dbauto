package com.rdxer.db.auto;

import com.rdxer.db.auto.annotation.AutoColumn;
import com.rdxer.db.auto.annotation.AutoColumnIgnore;
import com.rdxer.db.auto.annotation.AutoTable;
import com.rdxer.db.auto.model.FieldMeta;
import com.rdxer.db.auto.model.TableMeta;
import com.rdxer.db.auto.utils.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
public class DBAutoManager {

    public enum DbType {
        MySQL(new AutoGenByMySQL()), PGSQL(new AutoGenByPGSQL());

        public BaseAutoGen gen;

        DbType(BaseAutoGen gen) {
            this.gen = gen;
        }
    }

    public static String mainPackage;

    public DbType type;

    public DBAutoManager setDbType(DbType type) {
        this.type = type;
        return this;
    }

    // 事务支持的 Template
    @Autowired
    private TransactionTemplate transactionTemplate;
    // 默认jdbc
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private List<String> packageScan = new ArrayList<>();

    public List<String> getPackageScan() {
        return packageScan;
    }

    public DBAutoManager addPackageScan(String... packageScan) {
        this.packageScan.addAll(Arrays.stream(packageScan).toList());
        return this;
    }

    public void run() {
        Set<Class<?>> classes = new HashSet<>();
        getPackageScan().add(mainPackage);
//        mainPackage

        if (getPackageScan().isEmpty()) {
            throw new RuntimeException("启动DBAuth，但是找不到扫描的包，或者未设置");
        }

        for (String pack : getPackageScan()) {
            classes.addAll(ClassUtil.getClasses(pack));
        }

        List<Class<?>> needAutoClass = new ArrayList<>();
        List<TableMeta> tableMetas = new ArrayList<>();

        for (Class<?> aClass : classes) {

            Annotation[] declaredAnnotations = aClass.getDeclaredAnnotations();
            AutoTable table = aClass.getDeclaredAnnotation(AutoTable.class);
            if (table == null) {
                continue;
            }
            TableMeta tableMeta = new TableMeta();
            tableMeta.setClazz(aClass);
            tableMeta.setName(table.name());
            tableMeta.setComment(table.comment());
            tableMeta.setFieldList(new ArrayList<>());

//            System.out.println(aClass.getName() + table.name() + table.comment());
            for (Field field : aClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                AutoColumnIgnore ignore = field.getDeclaredAnnotation(AutoColumnIgnore.class);
                if (ignore != null) {
                    continue;
                }
//                System.out.print(field.getName() + " ");

                FieldMeta fieldMeta = new FieldMeta();

                AutoColumn autoColumn = field.getDeclaredAnnotation(AutoColumn.class);

                fieldMeta.setColumn(autoColumn);
                fieldMeta.setField(field);
                tableMeta.getFieldList().add(fieldMeta);


            }
            if (!tableMeta.getFieldList().isEmpty()) {
                tableMetas.add(tableMeta);
                needAutoClass.add(aClass);
            }
        }

        if (type == null) {
            type = DbType.MySQL;
        }

        type.gen.autoGen(tableMetas, needAutoClass, jdbcTemplate, transactionTemplate);
    }


}
