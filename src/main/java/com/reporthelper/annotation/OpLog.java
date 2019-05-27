package com.reporthelper.annotation;

import java.lang.annotation.*;

/**
 * 系统操作日志注解
 *
 * @author Myron Miao
 * @date 2019-03-25
 **/
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {
    /**
     * 操作日志名称
     *
     * @return
     */
    String name() default "";

    /**
     * 操作日常说明
     *
     * @return
     */
    String desc() default "";
}
