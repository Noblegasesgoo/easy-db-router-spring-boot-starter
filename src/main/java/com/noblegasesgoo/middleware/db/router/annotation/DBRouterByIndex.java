package com.noblegasesgoo.middleware.db.router.annotation;

import java.lang.annotation.*;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:44
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouter
 * @Description: index-type注解，通过key-index与key-type来唯一确认被路由的键，
 *               如果入参是多个基本类型形参，请用该注解。
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouterByIndex {

    /**
     * 该字段在方法中对应参数位置下标，从0开始
     */
    String keyIndex() default "0";

    /**
     * 该字段的类型
     */
    String keyType() default "String";
}
