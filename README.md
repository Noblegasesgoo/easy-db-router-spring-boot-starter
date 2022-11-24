

# 🕐项目介绍

😊这是一个可以开箱即用的微型数据库路由中间件🍽，内有丰富注释，为个人学习所搭建，也欢迎 `fork` 到本地自己研究，大家一起努力一起变好😊。

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

- 配置 `yml/yaml` 文件：

```yaml
# 多数据源路由配置
my-db-router:
  jdbc:
    datasource:
      # 分库数，必须得是 2 的整数次幂
      dbCount: 2
      # 分表数，必须得是 2 的整数次幂
      tableCount: 4
      # 默认使用的库
      default: db00
      # 分库列表
      list: db01,db02
      # 分库索引范围(1 - N)
      # 分表索引范围(0 - N)
      # 默认库（不计入分库数量）
      db00:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://127.0.0.1:3306/xxx?useUnicode=true&serverTimezone=Asia/Shanghai
        username: 
        password: 
      # 分库一
      db01:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://127.0.0.1:3306/xxx?useUnicode=true&serverTimezone=Asia/Shanghai
        username: 
        password: 
      # 分库二
      db02:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://127.0.0.1:3306/xxx?useUnicode=true&serverTimezone=Asia/Shanghai
        username: 
        password: 
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



- `@DBRouterStrategy`注解用法：

```java
// 启动对于User表的分表，不加该注解默认User表只被分库，并且分库之后的表名和原表明一致
@DBRouterStrategy(splitTable = true)
public interface IUserDao {
    ...
}
```

```
// 下面这种情况的分库一定要加注解 `@DBRouterStrategy`
- db00:
-- test_table 

- db01:
-- test_table_000 
-- test_table_001

- db02:
-- test_table_000 
-- test_table_001

-------------------------------------------

// 下面这种情况的分库不要加注解 `@DBRouterStrategy`
- db00:
-- test_table // 分库之后的表名和原表明一致

- db01:
-- test_table // 分库之后的表名和原表明一致

- db02:
-- test_table // 分库之后的表名和原表明一致
```





# 🕒历史版本介绍



### ✨v1

- 开箱即用，支持分库分表索引计算。
- 支持应对多数据源的编程式事务。
- 分库分表时，对应分库的下标必须从 `1` 开始，对应分表的下标必须从`0`开始。
- 库表数量均得为 `2^n`。

- 仅支持使用 `key` 字段 所对应的字段名称来匹配需要用于分库分表的方法参数，仅支持String类型的参数。



### ✨v2

- `v1` 基础上新增: 抛弃 `key` 字段，引入 `keyIndex` 与 `keyType` 来**唯一确认**需要用于分库分表的方法参数，支持 `int、short、long、float、double、char、String、boolean` 类型。



### ✨v2.1

- `v2.1` 基础上新增注解: 支持通过**对象**确认需要用于分库分表的方法参数。



# 🕓待开发日程

- **重构部分内容，脱离借鉴部分的束缚**。
- 适用于数据库下标以及表下标从 `0 - n` 或者从 `2 - n` 的方式来散列到对应库表的功能。
- 自定义**分库下标与分表下标**。

