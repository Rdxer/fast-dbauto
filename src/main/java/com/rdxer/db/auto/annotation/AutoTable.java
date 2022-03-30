package com.rdxer.db.auto.annotation;

import java.lang.annotation.*;


//表示注解加在接口、类、枚举等
@Target(ElementType.TYPE)
//VM将在运行期也保留注释，可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
//将此注解包含在javadoc中
@Documented
//允许子类继承父类中的注解
@Inherited
public @interface AutoTable {
    public String name() default "" ;

    public String comment() default "" ;
}