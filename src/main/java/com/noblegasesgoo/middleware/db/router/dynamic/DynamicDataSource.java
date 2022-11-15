package com.noblegasesgoo.middleware.db.router.dynamic;

import com.noblegasesgoo.middleware.db.router.DBContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 17:19
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DynamicDataSource
 * @Description: 动态数据源获取，每当切换数据源，都要从这个里面进行获取
 */

public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 获取当前sql对应数据源数据库
     * @return 当前sql对应数据源数据库
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return "db" + DBContextHolder.getDBKey();
    }
}
