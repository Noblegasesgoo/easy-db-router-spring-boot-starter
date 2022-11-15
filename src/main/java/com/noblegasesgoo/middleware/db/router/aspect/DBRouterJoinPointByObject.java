package com.noblegasesgoo.middleware.db.router.aspect;

import com.noblegasesgoo.middleware.db.router.annotation.DBRouterByObject;
import com.noblegasesgoo.middleware.db.router.config.DBRouterConfig;
import com.noblegasesgoo.middleware.db.router.strategy.IDBRouterStrategy;
import org.apache.commons.beanutils.BeanUtils;
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
import java.util.Objects;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月15日 15:34
 * @ProjectName: easy-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: DBRouterJoinPointByObject
 * @Description: 通过key来确认数据路由的切面
 */

@Aspect
public class DBRouterJoinPointByObject {

    private Logger logger = LoggerFactory.getLogger(DBRouterJoinPointByObject.class);

    /**
     * 数据路由配置
     */
    private DBRouterConfig dbRouterConfig;

    /**
     * 路由策略
     */
    private IDBRouterStrategy dbRouterStrategy;

    public DBRouterJoinPointByObject(DBRouterConfig dbRouterConfig, IDBRouterStrategy dbRouterStrategy) {
        this.dbRouterConfig = dbRouterConfig;
        this.dbRouterStrategy = dbRouterStrategy;
    }

    /**
     * 切点
     */
    @Pointcut("@annotation(com.noblegasesgoo.middleware.db.router.annotation.DBRouterByObject)")
    public void aopPointDBRouterObject() {
    }

    /**
     * 所有需要分库分表的操作，都需要使用自定义注解进行拦截，拦截后读取方法中的入参字段，根据字段进行路由操作。
     * 1. dbRouter.key() 确定根据哪个字段进行路由
     * 2. getAttrValue 根据数据库路由字段，从入参中读取出对应的值。比如路由 key 是 uId，那么就从入参对象 Obj 中获取到 uId 的值。
     * 3. dbRouterStrategy.doRouter(dbKeyAttr) 路由策略根据具体的路由值进行处理
     * 4. 路由处理完成比，就是放行。 jp.proceed();
     * 5. 最后 dbRouterStrategy 需要执行 clear 因为这里用到了 ThreadLocal 需要手动清空。关于 ThreadLocal 内存泄漏介绍 https://t.zsxq.com/027QF2fae
     */
    @Around("aopPointDBRouterObject() && @annotation(dbRouterObject)")
    public Object doRouterByObject(ProceedingJoinPoint jp, DBRouterByObject dbRouterObject) throws Throwable {

        // 确定根据哪个字段进行数据库的路由
        String dbRouterKey = dbRouterObject.key();

        // 检查当前的 key 以及路由配置类中对应的路由key是否存在了
        if (StringUtils.isBlank(dbRouterKey) && StringUtils.isBlank(dbRouterConfig.getKey())) {
            throw new RuntimeException("annotation DBRouter key is null！");
        }

        // 最终确定数据库路由字段的值，要么从注解获取，要么从配置类中获取
        dbRouterKey = StringUtils.isNotBlank(dbRouterKey) ? dbRouterKey : dbRouterConfig.getKey();

        // 根据数据库路由字段，从入参中读取出对应的值。
        String dbKeyAttr = null;
        if (getAttrValue1(dbRouterKey, jp.getArgs()) instanceof String) {
            dbKeyAttr = getAttrValue1(dbRouterKey, jp.getArgs());
        } else {
            dbKeyAttr = String.valueOf(getAttrValue1(dbRouterKey, jp.getArgs()));
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
     * @param dbRouterKey 被进行路由的字段
     * @param args        被拦截方法的入参
     * @return 对应路由 key 在入参中的值
     */
    public <T> T getAttrValue1(String dbRouterKey, Object[] args) {

        // 循环比对参数
        T filedValue = null;
        for (Object arg : args) {
            try {
                if (!Objects.isNull(filedValue)) {
                    break;
                }
                // 无论使用哪种属性引用格式，都以 String 的形式返回指定 Bean 的指定属性的值。
                // 也就是我们这里要用这个方法找到对应路由dbRouterKey的值在参数中的对应值。
                // 使用这个方法可以增加容错率，如果我入参是整形，但是通过这个方法可以返回该整形数值对应的字符串。
                filedValue = (T) BeanUtils.getProperty(arg, dbRouterKey);
            } catch (Exception e) {
                logger.error("获取路由属性值失败 dbRouterKey：{}", dbRouterKey, e);
            }
        }

        return filedValue;
    }
}
