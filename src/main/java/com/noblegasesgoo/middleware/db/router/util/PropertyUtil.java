package com.noblegasesgoo.middleware.db.router.util;

import com.noblegasesgoo.middleware.db.router.common.Constant;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author: noblegasesgoo
 * @CreateDate: 2022年11月10日 15:14
 * @ProjectName: my-db-router-spring-boot-starter
 * @version: 0.0.1
 * @FileName: PropertyUtil
 * @Description: 属性工具类
 */

public class PropertyUtil {

    /**
     * SpringBoot版本
     */
    private static int springBootVersion = Constant.ONE;

    static {
        try {
            // 判断是否为1.x版本的SpringBoot
            // 通过 环境变量读取 和 属性对象的绑定 RelaxedPropertyResolver 的这个类来判断SpringBoot版本
            Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
        } catch (ClassNotFoundException e) {
            // 否则此时的Springboot为2.x版本
            springBootVersion = Constant.TWO;
        }
    }

    /**
     * SpringBoot 1.x通过使用Java Reflect与SpringBoot 2.x兼容。
     * @param environment : 环境上下文
     * @param prefix      : 属性名称前缀
     * @param targetClass : 结果的目标类类型
     * @param <T>         : 参考 targetClass
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T handle(final Environment environment, final String prefix, final Class<T> targetClass) {
        switch (springBootVersion) {
            case 1:
                return (T) v1(environment, prefix);
            default:
                return (T) v2(environment, prefix, targetClass);
        }
    }

    private static Object v1(final Environment environment, final String prefix) {
        try {
            // 获取可以从环境变量读取和属性对象的绑定的RelaxedPropertyResolver类信息
            Class<?> resolverClass = Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");

            // 获取对应的解析器的构造器
            Constructor<?> resolverConstructor = resolverClass.getDeclaredConstructor(PropertyResolver.class);
            // 获得对应的解析器中名为 'getSubProperties' 的方法，并且是 'getSubProperties(String xxx)' 的方法
            Method getSubPropertiesMethod = resolverClass.getDeclaredMethod("getSubProperties", String.class);

            // 通过环境对象实例化一个解析器对象
            Object resolverObject = resolverConstructor.newInstance(environment);

            // 计算前缀，前缀格式为：xxx.xxx.
            String prefixParam = prefix.endsWith(".") ? prefix : prefix + ".";

            // 反射调用 resolverObject 对象中参数为 prefixParam 的 getSubProperties 方法
            return getSubPropertiesMethod.invoke(resolverObject, prefixParam);

        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private static Object v2(final Environment environment, final String prefix, final Class<?> targetClass) {
        try {
            // 获取Binder类信息
            Class<?> binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");

            // 获取Binder中参数为 Environment 对象并且名称叫做 get 的方法对象
            Method getMethod = binderClass.getDeclaredMethod("get", Environment.class);
            // 获取Binder中参数为 String，Class 对象并且名称叫做 bind 的方法对象
            Method bindMethod = binderClass.getDeclaredMethod("bind", String.class, Class.class);

            // 静态方法，可以忽略第一个参数调用，类信息已经被加载了
            Object binderObject = getMethod.invoke(null, environment);

            // 计算前缀，前缀格式为：xxx.xxx.xxx
            String prefixParam = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;

            // 通过 binderObject 调用 bind(String name, Class<T> target) 方法，方法参数为 prefixParam, targetClass。
            // 获取方法返回对象
            Object bindResultObject = bindMethod.invoke(binderObject, prefixParam, targetClass);

            // 获得 bindResultObject 对象中的 get 方法
            Method resultGetMethod = bindResultObject.getClass().getDeclaredMethod("get");

            // 通过 bindResultObject 调用 get 方法从指定的环境创建新的绑定程序实例。
            return resultGetMethod.invoke(bindResultObject);

        } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
