package com.noblegasesgoo.middleware.db.router.annotation;

import java.lang.annotation.*;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月15日 15:32
 * @ProjectName: easy-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouterObject
 * @Description: 通过key的名称来确认唯一被路由的键，如果参数是对象，想将对象中的某个属性作为被路由的键，请用此注解。
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouterByObject {

    /**
     * 被用来做分库分表的字段
     */
    String key();

    /**
     * 当前参数class
     */
    @Deprecated
    Class sourceClass();
}
