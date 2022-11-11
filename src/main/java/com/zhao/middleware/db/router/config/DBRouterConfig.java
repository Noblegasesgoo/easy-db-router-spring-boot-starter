package com.zhao.middleware.db.router.config;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:48
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouterConfig
 * @Description: 数据路由配置，后期从yml配置文件中读取，这样也可以在配置中心随时热更新
 */

public class DBRouterConfig {

    /**
     * 分库数量
     */
    private int dbCount;

    /**
     * 分表数量
     */
    private int tableCount;

    /**
     * 路由字段
     */
    private String routerKey;

    public DBRouterConfig() {
    }

    public DBRouterConfig(int dbCount, int tableCount, String routerKey) {
        this.dbCount = dbCount;
        this.tableCount = tableCount;
        this.routerKey = routerKey;
    }

    public int getDbCount() {
        return dbCount;
    }

    public void setDbCount(int dbCount) {
        this.dbCount = dbCount;
    }

    public int getTableCount() {
        return tableCount;
    }

    public void setTableCount(int tbCount) {
        this.tableCount = tbCount;
    }

    public String getRouterKey() {
        return routerKey;
    }

    public void setRouterKey(String routerKey) {
        this.routerKey = routerKey;
    }
}
