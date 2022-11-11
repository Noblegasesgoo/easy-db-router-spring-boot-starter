package com.zhao.middleware.db.router.strategy.impl;

import com.zhao.middleware.db.router.DBContextHolder;
import com.zhao.middleware.db.router.common.Constant;
import com.zhao.middleware.db.router.config.DBRouterConfig;
import com.zhao.middleware.db.router.strategy.IDBRouterStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:53
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouterStrategyHashCode
 * @Description: 哈希路由策略
 */

public class DBRouterStrategyHashCode implements IDBRouterStrategy {

    private Logger logger = LoggerFactory.getLogger(DBRouterStrategyHashCode.class);

    /**
     * 需要数据路由配置
     */
    private DBRouterConfig dbRouterConfig;

    public DBRouterStrategyHashCode(DBRouterConfig dbRouterConfig) {
        this.dbRouterConfig = dbRouterConfig;
    }

    @Override
    public void doRouter(String dbKeyAttr) {

        // 计算整个分库分表之后的二维大小，把它当成 HashMap 一样的长度进行使用
        int size = dbRouterConfig.getDbCount() * dbRouterConfig.getTableCount();

        // 借用 JDK1.8 的 HashMap 的散列扰动算法来使我们的散列更加平均
        logger.info("当前dbKeyAttr的值为 ：{}", dbKeyAttr);
        logger.info("当前dbKeyAttr的hashcode的值为 ：{}", dbKeyAttr.hashCode());
        int idx = (size - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));

        // 库表索引；
        // 相当于是把一个长条的桶，切割成段，对应分库分表中的库编号和表编号
        // 计算库索引
        int dbIdx = idx / dbRouterConfig.getDbCount();
        // 库索引边界判断
        if (dbIdx == Constant.ZERO) {
            dbIdx += 1;
        }
        if (dbIdx > dbRouterConfig.getDbCount()) {
            dbIdx = dbRouterConfig.getDbCount();
        }

        // 计算库中表索引
        int tableIdx = idx - dbRouterConfig.getDbCount() * (dbIdx - 1);
        // 表索引边界判断，此时默认表编号从0开始
        if (tableIdx > dbRouterConfig.getTableCount()) {
            tableIdx = dbRouterConfig.getTableCount() - 1;
        }

        // TODO：之后开发适用于编号从0开始或从1开始的不同边界散列库表索引的方法。

        // 设置到数据源上下文中去
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
        DBContextHolder.setTableKey(String.format("%03d", tableIdx));
        logger.info("数据库路由 dbIdx：{} tbIdx：{}",  dbIdx, tableIdx);
    }

    @Override
    public void setDBKey(int dbIdx) {
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
    }

    @Override
    public void setTableKey(int tableIdx) {
        DBContextHolder.setTableKey(String.format("%03d", tableIdx));
    }

    @Override
    public int getDBCount() {
        return dbRouterConfig.getDbCount();
    }

    @Override
    public int getTableCount() {
        return dbRouterConfig.getTableCount();
    }

    @Override
    public void clearRouter() {
        DBContextHolder.clearDBKey();
        DBContextHolder.clearTableKey();
    }
}
