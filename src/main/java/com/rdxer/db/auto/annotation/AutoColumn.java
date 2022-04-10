package com.rdxer.db.auto.annotation;

import java.lang.annotation.*;

// 表示可用于字段
@Target(ElementType.FIELD)
// VM将在运行期也保留注释，因此可以通过反射机制读取注解的信息
@Retention(RetentionPolicy.RUNTIME)
// 将此注解包含在javadoc中
@Documented
// 允许子类继承父类中的注解
@Inherited
public @interface AutoColumn {

    /**
     * 字段名
     *
     * @return 字段名
     */
    public String name() default "" ;


    /**
     * 是否为可以为null，true是可以，false是不可以，默认为true
     *
     * @return 是否为可以为null，true是可以，false是不可以，默认为true
     */
    public boolean isNull() default true;

    /**
     * 是否是主键，默认false
     *
     * @return 是否是主键，默认false
     */
    public boolean isKey() default false;

    /**
     * 是否自动递增，默认false 只有主键才能使用
     *
     * @return 是否自动递增，默认false 只有主键才能使用
     */
    public boolean isAutoIncrement() default false;

    /**
     * 是否是唯一，默认false
     *
     * @return 是否是唯一，默认false
     */
    public boolean isUnique() default false;


    /**
     * 除了字段名其他全部失效
     *
     * @return 字段 类型自定义
     */
    public String define() default "" ;

 
    public String comment() default "" ;

    /**
     * 长度 1  or  1,1
     *
     * @return 长度
     */
    public String len() default "" ;
}