package com.noblegasesgoo.middleware.db.router.config;

import com.noblegasesgoo.middleware.db.router.aspect.DBRouterJoinPointByIndex;
import com.noblegasesgoo.middleware.db.router.aspect.DBRouterJoinPointByObject;
import com.noblegasesgoo.middleware.db.router.common.BaseConstants;
import com.noblegasesgoo.middleware.db.router.dynamic.DynamicDataSource;
import com.noblegasesgoo.middleware.db.router.dynamic.DynamicMybatisPlugin;
import com.noblegasesgoo.middleware.db.router.strategy.IDBRouterStrategy;
import com.noblegasesgoo.middleware.db.router.strategy.impl.DBRouterStrategyHashCode;
import com.noblegasesgoo.middleware.db.router.util.PropertyUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 19:00
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DataSourceAutoConfig
 * @Description: 数据源配置解析自动配置类
 */

@Configuration
public class DataSourceAutoConfig implements EnvironmentAware {

    /**
     * 数据源配置组
     */
    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<>();

    /**
     * 默认数据源配置
     */
    private Map<String, Object> defaultDataSourceConfig;

    /**
     * 分库数量
     */
    private int dbCount;

    /**
     * 分表数量
     */
    private int tableCount;

    @Bean(name = "dbRouterConfig")
    public DBRouterConfig dbRouterConfig() {
        return new DBRouterConfig(dbCount, tableCount);
    }

    @Bean(name = "dbRouterStrategy")
    public IDBRouterStrategy dbRouterStrategy(DBRouterConfig dbRouterConfig) {
        return new DBRouterStrategyHashCode(dbRouterConfig);
    }

    @Bean(name = "dynamicMybatisPlugin")
    public Interceptor plugin() {
        return new DynamicMybatisPlugin();
    }

    @Bean(name = "dbRouterPointByIndex")
    @ConditionalOnMissingBean
    public DBRouterJoinPointByIndex dbRouterPointByIndex(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        return new DBRouterJoinPointByIndex(dbRouterConfig, dbRouterStrategy);
    }

    @Bean(name = "dbRouterPointByObject")
    @ConditionalOnMissingBean
    public DBRouterJoinPointByObject dbRouterPointByObject(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        return new DBRouterJoinPointByObject(dbRouterConfig, dbRouterStrategy);
    }

    @Override
    public void setEnvironment(Environment environment) {
        String prefix = BaseConstants.PREFIX;

        dbCount = Integer.valueOf(environment.getProperty(prefix + "dbCount"));
        tableCount = Integer.valueOf(environment.getProperty(prefix + "tableCount"));

        // 分库分表数据源
        String dataSources = environment.getProperty(prefix + "list");
        assert dataSources != null;
        for (String dbInfo : dataSources.split(",")) {
            Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + dbInfo, Map.class);
            dataSourceMap.put(dbInfo, dataSourceProps);
        }

        // 默认数据源
        String defaultData = environment.getProperty(prefix + "default");
        defaultDataSourceConfig = PropertyUtil.handle(environment, prefix + defaultData, Map.class);
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() {

        // 创建数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        for (String dbInfo : dataSourceMap.keySet()) {
            Map<String, Object> objMap = dataSourceMap.get(dbInfo);
            targetDataSources.put(dbInfo,
                    new DriverManagerDataSource(objMap.get("url").toString()
                            , objMap.get("username").toString()
                            , objMap.get("password").toString()));
        }

        // 设置数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(
                new DriverManagerDataSource(defaultDataSourceConfig.get("url").toString()
                        , defaultDataSourceConfig.get("username").toString()
                        , defaultDataSourceConfig.get("password").toString()));

        return dynamicDataSource;
    }


    /**
     * 把数据源的切换放在事务处理前，而事务操作也通过编程式编码进行处理
     */
    @Bean(name = "transactionTemplate")
    public TransactionTemplate transactionTemplate(DataSource dataSource) {
        // Spring提供的TransactionTemplate 能够以编程的方式实现事务控制，是无状态而且线程安全的
        // 设置数据源事务管理器，面对JDBC，一次设置一个数据源
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        // 编程式事务设置
        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(dataSourceTransactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        return transactionTemplate;
    }

}
