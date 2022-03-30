package com.rdxer.db.auto.annotation;

import java.lang.annotation.*;

// 表示可用于字段
@Target(ElementType.FIELD)
// VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AutoColumnIgnore {

}