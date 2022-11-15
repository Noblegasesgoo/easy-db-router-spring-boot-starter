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
    @Deprecated
    String key() default "";

    /**
     * 该字段在方法中对应参数位置下标，从0开始
     */
    String keyIndex() default "0";

    /**
     * 该字段的类型
     */
    String keyType() default "String";
}
