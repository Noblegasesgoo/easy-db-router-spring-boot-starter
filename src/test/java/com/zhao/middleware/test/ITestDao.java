package com.zhao.middleware.test;

import com.zhao.middleware.db.router.annotation.DBRouter;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 19:58
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: ITestDao
 * @Description:
 */

public interface ITestDao {

    @DBRouter(key = "uid")
    void test();
}
