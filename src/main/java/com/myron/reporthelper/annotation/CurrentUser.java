package com.myron.reporthelper.annotation;


import com.myron.reporthelper.consts.UserAuthConsts;

import java.lang.annotation.*;

/**
 * 绑定当前登录的用户
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

    /**
     * 当前用户在request中的名字
     *
     * @return
     */
    String value() default UserAuthConsts.CURRENT_USER;

}
