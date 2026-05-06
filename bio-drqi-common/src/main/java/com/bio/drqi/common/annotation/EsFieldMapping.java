package com.bio.drqi.common.annotation;

import com.bio.drqi.common.enums.EsFieldTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EsFieldMapping {

    EsFieldTypeEnum type() default EsFieldTypeEnum.AUTO;

    boolean index() default true;

    int ignoreAbove() default -1;

    String analyzer() default "";

    String searchAnalyzer() default "";

    String format() default "";
}
