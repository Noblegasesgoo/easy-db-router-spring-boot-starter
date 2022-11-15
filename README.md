

# 🕐项目介绍

😊这是一个真正可以开箱即用的微型数据库路由中间件🍽，内有丰富注释，为个人学习所搭建，也欢迎 `fork` 到本地自己研究，大家一起努力一起变好😊。

希望大家能够指出缺点，提交`PR`。



# 🕑使用方式

- 大家将代码拉到本地进行 `Maven` 打包，引入依赖即可开箱即用：

```xml
<dependency>
    <groupId>com.noblegaesgoo</groupId>
    <artifactId>easy-db-router-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```



- `@DBRouterByIndex`注解使用案例：

```java
// 将'queryUser'方法中下标为'0'并且为'String'类型的参数作为分库分表键
@DBRouterByIndex(keyIndex = "0", keyType = TypeConstants.STRING)
List<User> queryUser(String uId);
```



- `@DBRouterByObject`注解使用案例：

```java
// 将'user'对象中名称为'uId'的字段作为分库分表键
@DBRouterByObject(key = "uId")
void insert(User user);
```



- `@DBRouterStrategy`注解使用案例：

```java
// 启动对于User表的分表
@DBRouterStrategy(splitTable = true)
public interface IUserDao {
    ...
}
```



# 🕒历史版本介绍



### ✨v1 - 221111

- 开箱即用，支持分库分表索引计算。
- 支持应对多数据源的编程式事务。
- 分库分表时，对应分库的下标必须从 `1` 开始，对应分表的下标必须从`0`开始。
- 库表数量均得为 `2^n`。

- 仅支持使用 `key` 字段 所对应的字段名称来匹配需要用于分库分表的方法参数，仅支持String类型的参数。



### ✨v2 - 221115

- `v1` 基础上新增: 抛弃 `key` 字段，引入 `keyIndex` 与 `keyType` 来**唯一确认**需要用于分库分表的方法参数，支持 `int、short、long、float、double、char、String、boolean` 类型。



### ✨v2.1 - 221115

- `v2.1` 基础上新增注解: 支持通过**对象**确认需要用于分库分表的方法参数。



# 🕓待开发日程

- **重构部分内容，脱离借鉴部分的束缚**。
- 适用于数据库下标以及表下标从 `0 - n` 或者从 `1 - n` 的方式来散列到对应库表的功能。
- 自定义**分库下标与分表下标**。

