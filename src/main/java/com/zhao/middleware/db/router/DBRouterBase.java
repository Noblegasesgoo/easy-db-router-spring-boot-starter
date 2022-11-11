package com.zhao.middleware.db.router;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:49
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouterBase
 * @Description: 数据源基础配置
 */

public class DBRouterBase {

    /**
     * 表索引
     */
    private String tableIdx;

    public String getTableIdx() {
        return DBContextHolder.getTableKey();
    }
}
