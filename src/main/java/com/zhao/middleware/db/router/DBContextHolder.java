package com.zhao.middleware.db.router;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:45
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBContextHolder
 * @Description: 数据源上下文，使用ThreadLocal来保证数据传输的过程线程安全
 */

public class DBContextHolder {

    /**
     * 数据库键
     */
    private static final ThreadLocal<String> dbKey = new ThreadLocal<String>();

    /**
     * 表键
     */
    private static final ThreadLocal<String> tableKey = new ThreadLocal<String>();

    public static void setDBKey(String dbKeyIdx) {
        dbKey.set(dbKeyIdx);
    }

    public static String getDBKey() {
        return dbKey.get();
    }

    public static void setTableKey(String tbKeyIdx) {
        tableKey.set(tbKeyIdx);
    }

    public static String getTableKey() {
        return tableKey.get();
    }

    public static void clearDBKey() {
        dbKey.remove();
    }

    public static void clearTableKey() {
        tableKey.remove();
    }
}
