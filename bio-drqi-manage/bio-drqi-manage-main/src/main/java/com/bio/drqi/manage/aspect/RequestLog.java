package com.bio.drqi.manage.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ZouJun
 * @version V1.0
 * @Description: TODO
 * @Date: 2022/3/28 9:54
 * @ClassName: Log
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLog {
    String value() default "";
}
