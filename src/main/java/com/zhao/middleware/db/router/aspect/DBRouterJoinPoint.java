package com.zhao.middleware.db.router.aspect;

import com.zhao.middleware.db.router.annotation.DBRouter;
import com.zhao.middleware.db.router.common.Constant;
import com.zhao.middleware.db.router.config.DBRouterConfig;
import com.zhao.middleware.db.router.strategy.IDBRouterStrategy;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 13:50
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouterJoinPoint
 * @Description: 数据路由切面，通过自定义注解的方式，拦截被切面的方法，进行数据库路由
 */

@Aspect
public class DBRouterJoinPoint {

    private Logger logger = LoggerFactory.getLogger(DBRouterJoinPoint.class);

    /**
     * 数据路由配置
     */
    private DBRouterConfig dbRouterConfig;

    /**
     * 路由策略
     */
    private IDBRouterStrategy dbRouterStrategy;

    public DBRouterJoinPoint(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }

    private static Map<String, Class> typeMap = new ConcurrentHashMap<String, Class>() {
        {
            put("java.lang.Integer", Integer.class);
            put("java.lang.Double", Double.class);
            put("java.lang.Float", Float.class);
            put("java.lang.Long", Long.class);
            put("java.lang.Short", Short.class);
            put("java.lang.Boolean", Boolean.class);
            put("java.lang.Character", Character.class);
            put("java.lang.String", String.class);
        }
    };

    /**
     * 切点
     */
    @Pointcut("@annotation(com.zhao.middleware.db.router.annotation.DBRouter)")
    public void aopPoint() {
    }

    /**
     * 所有需要分库分表的操作，都需要使用自定义注解进行拦截，拦截后读取方法中的入参字段，根据字段进行路由操作。
     * 1. dbRouter.key() 确定根据哪个字段进行路由
     * 2. getAttrValue 根据数据库路由字段，从入参中读取出对应的值。比如路由 key 是 uId，那么就从入参对象 Obj 中获取到 uId 的值。
     * 3. dbRouterStrategy.doRouter(dbKeyAttr) 路由策略根据具体的路由值进行处理
     * 4. 路由处理完成比，就是放行。 jp.proceed();
     * 5. 最后 dbRouterStrategy 需要执行 clear 因为这里用到了 ThreadLocal 需要手动清空。关于 ThreadLocal 内存泄漏介绍 https://t.zsxq.com/027QF2fae
     */
    @Around("aopPoint() && @annotation(dbRouter)")
    public Object doRouter(ProceedingJoinPoint jp, DBRouter dbRouter) throws Throwable {

        // 确定根据哪个字段进行数据库的路由
        String keyIndex = dbRouter.keyIndex();
        String keyType = dbRouter.keyType();

        // 检查当前的 startIndex 以及路由配置类中对应的路由startIndex是否存在了
        if (StringUtils.isBlank(keyIndex) && StringUtils.isBlank(dbRouterConfig.getKeyIndex())) {
            throw new RuntimeException("annotation DBRouter startIndex is null！");
        }

        // 最终确定数据库路由字段的值，要么从注解获取，要么从配置类中获取
        keyIndex = StringUtils.isNotBlank(keyIndex) ? keyIndex : dbRouterConfig.getKeyIndex();

        // 根据数据库路由字段，从入参中读取出对应的值。比如路由 key 是 uId，那么就从入参对象 Obj 中获取到 uId 的值。
        String dbKeyAttr = String.valueOf(getAttrValue(keyIndex, jp.getArgs(), typeMap.get(keyType).getName()));
        if (Constant.NULL.equals(dbKeyAttr)) {
            throw new RuntimeException("The parameter types of the labeled methods do not match!");
        }

        // 调用路由策略
        dbRouterStrategy.doRouter(dbKeyAttr);

        // 返回结果
        try {
            return jp.proceed();
        } finally {
            // 一定要进行ThreadLocal的内存回收
            dbRouterStrategy.clearRouter();
        }
    }

    /**
     * 获取切点的方法信息
     * @param jp 切点
     * @return 切点方法信息
     * @throws NoSuchMethodException
     */
    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }

    /**
     * 根据数据库路由字段，从入参中读取出对应的值
     * 比如路由 key 是 uId，那么就从入参对象 Obj 中获取到 uId 的值。
     * @param keyIndex 被进行路由的字段所在方法的参数下标
     * @param args 被拦截方法的入参
     * @return 对应路由 key 在入参中的值
     */
    public <T> T getAttrValue(String keyIndex, Object[] args, T t) {

        // 循环比对参数
        String name = args[Integer.parseInt(keyIndex)].getClass().getName();
        if (t.equals(name)) {
            return (T) args[Integer.parseInt(keyIndex)];
        }

        return null;
    }
}
