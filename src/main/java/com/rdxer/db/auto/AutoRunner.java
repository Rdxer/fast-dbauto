package com.rdxer.db.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
// 先刷新表
@Order(9)
public class AutoRunner implements ApplicationRunner {

    Logger logger = LoggerFactory.getLogger(AutoRunner.class);

    @Resource
    DBAutoManager autoManager;

    @Value(value = "${com.rdxer.db.auto.dbtype}")
    DBAutoManager.DbType dbType;

    @Value(value = "${com.rdxer.db.auto.enable:true}")
    Boolean enable;

    @Override
    public void run(ApplicationArguments args) {
        if (dbType == null) {
            String msg = "com.rdxer.db.auto.dbtype == null ,请配置 com.rdxer.db.auto.dbtype={PGSQL or MySQL}";
            logger.error(msg);
            throw new RuntimeException(msg);
        } else {
            logger.info("dbType: " + dbType);
        }
        if (!enable) {
            logger.warn("db.auto.enable: " + enable);
            return;
        }
        autoManager
                .setDbType(dbType)
                .run();
    }
}
