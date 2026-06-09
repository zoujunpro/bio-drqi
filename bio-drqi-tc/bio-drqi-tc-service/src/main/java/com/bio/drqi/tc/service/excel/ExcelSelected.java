package com.bio.drqi.tc.service.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSelected {

    String[] value();

    int firstRow() default 1;

    int lastRow() default 1000;
}
