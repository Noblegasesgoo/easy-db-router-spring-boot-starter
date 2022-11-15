package com.noblegasesgoo.middleware.db.router.strategy;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:51
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: IDBRouterStrategy
 * @Description: 路由策略
 */

public interface IDBRouterStrategy {

    /**
     * 路由计算
     * @param dbKeyAttr 路由字段的值
     */
    void doRouter(String dbKeyAttr);

    /**
     * 手动设置分库路由
     * @param dbIdx 路由库，需要在配置范围内
     */
    void setDBKey(int dbIdx);

    /**
     * 手动设置分表路由
     * @param tbIdx 路由表，需要在配置范围内
     */
    void setTableKey(int tbIdx);

    /**
     * 获取分库数
     * @return 数量
     */
    int getDBCount();

    /**
     * 获取分表数
     * @return 数量
     */
    int getTableCount();

    /**
     * 清除路由
     */
    void clearRouter();
}
