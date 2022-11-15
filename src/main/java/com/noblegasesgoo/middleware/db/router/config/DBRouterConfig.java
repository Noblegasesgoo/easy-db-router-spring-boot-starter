package com.noblegasesgoo.middleware.db.router.config;

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
    private String key;

    /**
     * 该字段在方法中对应参数位置下标
     */
    private String keyIndex;

    /**
     * 该字段的类型
     */
    private String keyType;

    /**
     * 当前参数class
     */
    @Deprecated
    private Class sourceClass;

    public DBRouterConfig() {
    }

    public DBRouterConfig(int dbCount, int tableCount) {
        this.dbCount = dbCount;
        this.tableCount = tableCount;
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

    public void setTableCount(int tableCount) {
        this.tableCount = tableCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyIndex() {
        return keyIndex;
    }

    public void setKeyIndex(String keyIndex) {
        this.keyIndex = keyIndex;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(Class sourceClass) {
        this.sourceClass = sourceClass;
    }
}
