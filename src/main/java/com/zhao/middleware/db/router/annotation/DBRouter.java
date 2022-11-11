package com.zhao.middleware.db.router.annotation;

import java.lang.annotation.*;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:44
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouter
 * @Description: 路由注解
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouter {

    /**
     * 被用来做分库分表的字段
     */
    String key() default "";
}
