package com.carverlee.pidan.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author carverLee
 * 2019/9/26 11:32
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target(ElementType.TYPE)
public @interface TimeCheck {
    CheckScope scope() default CheckScope.CLASS;

    String tag() default "pidan";

    String packageName() default "";
}
