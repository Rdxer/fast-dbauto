package com.rdxer.db.auto;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        SpringContextUtil.class,
        DBAutoManager.class,
        AutoRunner.class,
        GetPackage.class,
})
public @interface EnableDBAuto {

}