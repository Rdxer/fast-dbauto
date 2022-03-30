package com.rdxer.db.auto;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import static com.rdxer.db.auto.DBAutoManager.mainPackage;

public class GetPackage implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        mainPackage = ClassUtils.getPackageName(metadata.getClassName());
    }


}