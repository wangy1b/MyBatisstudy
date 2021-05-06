Mybatis

## 入门

### 安装

要使用 MyBatis， 只需将 [mybatis-x.x.x.jar](https://github.com/mybatis/mybatis-3/releases) 文件置于类路径（classpath）中即可。

如果使用 Maven 来构建项目，则需将下面的依赖代码置于 pom.xml 文件中：

```
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis</artifactId>
  <version>x.x.x</version>
</dependency>
```

编写mybatis工具类

~~~ java
public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            //使用mybatis获取SqlSessionFactory对象
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 既然有了 SqlSessionFactory，顾名思义，我们可以从中获得 SqlSession 的实例。
    // sqlSession完全包含了面向数据执行sql命令的所需的所有方法
    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
}
~~~

编写代码

* 实体类

  ~~~ java
  
  public class User {
      private int id;
      private String name;
      private String pwd;
  
      public User(int id, String name, String pwd) {
          this.id = id;
          this.name = name;
          this.pwd = pwd;
      }
  
      public int getId() {
          return id;
      }
  
      public void setId(int id) {
          this.id = id;
      }
  
      public String getName() {
          return name;
      }
  
      public void setName(String name) {
          this.name = name;
      }
  
      public String getPwd() {
          return pwd;
      }
  
      public void setPwd(String pwd) {
          this.pwd = pwd;
      }
  
  
      @Override
      public String toString() {
          return "User{" +
                  "id=" + id +
                  ", name='" + name + '\'' +
                  ", pwd='" + pwd + '\'' +
                  '}';
      }
  }
  ~~~

* Dao接口

  ~~~ java
  public interface UserDao {
      List<User> getUserList();
  }
  
  ~~~

* 接口实现类

  由原来的UserDaoImpl转变为一个Mapper配置文件

  ~~~ xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE mapper
          PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  
  <!--namespace 绑定一个对应Dao接口-->
  <mapper namespace="com.wyb.dao.UserDao">
      <!-- select 查询语句-->
      <!-- id="getUserList" 为查询方法名 -->
      <!-- resultType="com.wyb.pojo.User" 为返回结果类型 -->
      <select id="getUserList" resultType="com.wyb.pojo.User">
        SELECT * from mybatis.user
      </select>
  </mapper>
  ~~~

* 测试

  注意点

  1.绑定接口错误

  ~~~ 
  org.apache.ibatis.binding.BindingException: Type interface com.wyb.dao.UserDao is not known to the MapperRegistry.
  ~~~

  2.配置文件找不到

  ~~~ 
  Caused by: org.apache.ibatis.exceptions.PersistenceException: 
  ### Error building SqlSession.
  ### The error may exist in com/wyb/dao/UserMapper.xml
  ...
  Caused by: java.io.IOException: Could not find resource com/wyb/dao/UserMapper.xml
  ~~~

  maven由于约定大于配置，之后可能遇到写配置文件，无法被到处或者生效问题，解决方案

  ~~~
      <build>
          <resources>
              <resource>
                  <directory>src/main/resource</directory>
                  <includes>
                      <include>**/*.properties</include>
                      <include>**/*.xml</include>
                  </includes>
                  <filtering>true</filtering>
              </resource>
              <resource>
                  <directory>src/main/java</directory>
                  <includes>
                      <include>**/*.properties</include>
                      <include>**/*.xml</include>
                  </includes>
                  <filtering>true</filtering>
              </resource>
          </resources>
      </build>
  ~~~

* mysql

  ~~~ shell
  docker run -d --rm -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root --name mysql-test mysql:5.7
  
  
  create database mybatis;
  use mybatis;
  
  create table user(
  id int PRIMARY key,
  name varchar(20),
  pwd varchar(20)
  );
  
  
  select * from user;
  insert into user VALUES(1,'zhangsan','pwd1');
  insert into user VALUES(2,'lisi','pwd2');
  ~~~




## CRUD

namespace中的包名要和Dao/Mapper 接口中的包名一致

* select 	

```
id="getUserList"
```

```
resultType="com.wyb.pojo.User"
```

* insert
* delete
* update

万能Map 

假设实体类或者数据库中的表，字段或者参数过多，我们应当考虑使用map

~~~ xml
<update id="updateUser2" parameterType="map" >
    update mybatis.user set name = #{u_name} where id = #{u_id}
</update>
~~~

~~~ java  
    @Test
    public void updateUser2(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        Map<String,Object> map = new HashMap<>();
        map.put("u_name","sssssss");
        map.put("u_id",4);
        int res = mapper.updateUser2(map);
        System.out.println(res);
        sqlSession.commit();
        User userRes = mapper.getUserById(4);
        System.out.println(userRes.toString());
        sqlSession.close();
    }

~~~

模糊匹配

~~~ xml
    <!-- 通配符写道sql里能防止sql注入 -->
    <select id="getUserLike" resultType="com.wyb.pojo.User">
      SELECT * from mybatis.user where name like "%"#{value}"%"
    </select>
~~~



~~~ java
    @Test
    public void getUserLike(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> users = mapper.getUserLike("user");
        for (User user : users) {
            System.out.println(user.toString());
        }
        sqlSession.close();
    }
~~~



## 配置

MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。 配置文档的顶层结构如下：

- configuration（配置）
  - properties（属性）
  - settings（设置）
  - typeAliases（类型别名）
  - typeHandlers（类型处理器）
  - objectFactory（对象工厂）
  - plugins（插件）
  - environments（环境配置）
    - environment（环境变量）
      - transactionManager（事务管理器）
      - dataSource（数据源）
  - databaseIdProvider（数据库厂商标识）baseIdProvider)
  - mappers（映射器）

### 2.环境配置 environments

mybatis可以配置成适应多种环境

不过要记住：尽管可以配置多个环境，但每个SqlSessionFactory实例只能选择一种环境

Mybatis默认的事务管理器就是JDBC，连接池：POOLED

### 3.属性 properties

我们可以通过properties属性来实现引用配置文件

这些属性可以在外部进行配置，并可以进行动态替换。你既可以在典型的 Java 属性文件中配置这些属性，也可以在 properties 元素的子元素中设置

编写一个db.properties文件

~~~ properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybatis?useUnicode=true&charactoerEncoding=UTF-8"
username=root
password=root
~~~

在核心配置文件中引入

~~~ xml
<!--引用外部配置文件-->
<properties resource="db.properties">
    <!--<property name="username" value="dev_user"/>-->
    <!--<property name="password" value="F2Fa3!33TYyg"/>-->
</properties>
~~~



注意：

* 1.configuration文件的配置顺序

`The content of element type "configuration" must match "(properties?,settings?,typeAliases?,typeHandlers?,objectFactory?,objectWrapperFactory?,reflectorFactory?,plugins?,environments?,databaseIdProvider?,mappers?)"`

xml配置文件必须遵循properties?,settings?,typeAliases?,typeHandlers?,objectFactory?,objectWrapperFactory?,reflectorFactory?,plugins?,environments?,databaseIdProvider?,mappers?的顺序

* 可以直接银日外部文件
* 可以在其中增加一些属性配置
* 如果有相同属性在不同地方配置，优先使用外部配置文件的

### 4.类型别名 typeAliases

类型别名可为 Java 类型设置一个缩写名字。 它仅用于 XML 配置，意在降低冗余的全限定类名书写。

方式一：

~~~ xml
<!--可以给实体类取别名-->
<typeAliases>
    <typeAlias type="com.wyb.pojo.User" alias="User"></typeAlias>
</typeAliases>

~~~

方式二：

也可以指定一个包名，MyBatis 会在包名下面搜索需要的 Java Bean

~~~ xml
<typeAliases>
    <package name="com.wyb.pojo"/>
</typeAliases>
~~~

每一个在包 `com.wyb.pojo` 中的 Java Bean，在没有注解的情况下，会使用 Bean 的首字母小写的非限定类名来作为它的别名。 比如 `com.wyb.pojo.User` 的别名为 `user`；

若有注解，则别名为其注解值



在实体类比较少的时候，使用第一种方式，实体类比较多，建议使用第二种。

第一种可以DIY别名，第二种则不行，但是可以通过注解来取别名

~~~ java
@Alias("usr")
public class User {
    ...
}
~~~



### 5.设置 settings

这是 MyBatis 中极为重要的调整设置，它们会改变 MyBatis 的运行时行为。 下表描述了设置中各项设置的含义、默认值等。

| cacheEnabled                     | 全局性地开启或关闭所有映射器配置文件中已配置的任何缓存。     | true \| false                                                | true                                                  |
| -------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ----------------------------------------------------- |
| lazyLoadingEnabled               | 延迟加载的全局开关。当开启时，所有关联对象都会延迟加载。 特定关联关系中可通过设置 `fetchType` 属性来覆盖该项的开关状态。 | true \| false                                                | false                                                 |
| aggressiveLazyLoading            | 开启时，任一方法的调用都会加载该对象的所有延迟加载属性。 否则，每个延迟加载属性会按需加载（参考 `lazyLoadTriggerMethods`)。 | true \| false                                                | false （在 3.4.1 及之前的版本中默认为 true）          |
| useColumnLabel                   | 使用列标签代替列名。实际表现依赖于数据库驱动，具体可参考数据库驱动的相关文档，或通过对比测试来观察。 | true \| false                                                | true                                                  |
| useGeneratedKeys                 | 允许 JDBC 支持自动生成主键，需要数据库驱动支持。如果设置为 true，将强制使用自动生成主键。尽管一些数据库驱动不支持此特性，但仍可正常工作（如 Derby）。 | true \| false                                                | False                                                 |
| mapUnderscoreToCamelCase         | 是否开启驼峰命名自动映射，即从经典数据库列名 A_COLUMN 映射到经典 Java 属性名 aColumn。 | true \| false                                                | False                                                 |
| localCacheScope                  | MyBatis 利用本地缓存机制（Local Cache）防止循环引用和加速重复的嵌套查询。 默认值为 SESSION，会缓存一个会话中执行的所有查询。 若设置值为 STATEMENT，本地缓存将仅用于执行语句，对相同 SqlSession 的不同查询将不会进行缓存。 | SESSION \| STATEMENT                                         | SESSION                                               |
| logPrefix                        | 指定 MyBatis 增加到日志名称的前缀。                          | 任何字符串                                                   | 未设置                                                |
| logImpl                          | 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。        | SLF4J \| LOG4J \| LOG4J2 \| JDK_LOGGING \| COMMONS_LOGGING \| STDOUT_LOGGING \| NO_LOGGING | 未设置                                                |

### 6.其他配置

- [typeHandlers（类型处理器）](https://mybatis.org/mybatis-3/zh/configuration.html#typeHandlers)
- [objectFactory（对象工厂）](https://mybatis.org/mybatis-3/zh/configuration.html#objectFactory)
- [plugins（插件）](https://mybatis.org/mybatis-3/zh/configuration.html#plugins)

### 7.映射器 mappers

现在使用的是

MapperRegistry: 注册绑定我们的Mapper文件；

方式一：推荐使用

~~~ xml
<mappers>
    <mapper resource="com/wyb/dao/UserMapper.xml"/>
</mappers>
~~~

方式二：使用class文件绑定注册

~~~ xml
<mappers>
    <mapper class="com.wyb.dao.UserMapper"/>
</mappers>
~~~

注意点：

* 接口和他的Mapper配置文件必须同名
* 接口和他的Mapper配置文件必须在同一个包

方式三：使用package

~~~ xml
<mappers>
    <package name="com.wyb.dao"/>
</mappers>
~~~

注意点：

- 接口和他的Mapper配置文件必须同名
- 接口和他的Mapper配置文件必须在同一个包

### 生命周期和作用域

生命周期和作用域是至关重要的，因为错误的使用会导致非常严重的并发问题。

**SqlSessionFactoryBuilder**

* 一旦创建了 SqlSessionFactory，就不再需要它了
* 局部变量
* 

**SqlSessionFactory**

* 说白了就是数据库连接池
* SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建另一个实例
* SqlSessionFactory 的最佳作用域是应用作用域。
*  最简单的就是使用单例模式或者静态单例模式。

**SqlSession**

* SqlSession 的实例不是线程安全的，因此是不能被共享的，所以它的最佳的作用域是请求或方法作用域。
* 用完之后需要关闭



## 如何解决属性名和字段名不一致的问题

~~~ java
private int id;
private String name;
private String password;
~~~

数据库中

~~~ sql
create table user(
id int PRIMARY key,
name varchar(20),
pwd varchar(20)
);
~~~

运行的结果：

~~~ 
User{id=1, name='zhangsan', password='null'}
~~~

解决方法：

1.sql 中改别名

~~~ xml
    <select id="getUserById" parameterType="int" resultType="usr">
        SELECT id,name,pwd as password from mybatis.user where id = #{id}
    </select>
~~~



2.结果映射

`resultMap` 元素是 MyBatis 中最重要最强大的元素。

ResultMap 的设计思想是，对简单的语句做到零配置，对于复杂一点的语句，只需要描述语句之间的关系就行了。

一个需要显式配置 `ResultMap`，这就是 `ResultMap` 的优秀之处——你完全可以不用显式地配置它们。

之前你已经见过简单映射语句的示例，它们没有显式指定 `resultMap`

~~~ xml
<resultMap id="userMap" type="usr">
    <!-- column 为数据库中字段 property 为实体类中属性 -->
    <result column="id" property="id" />
    <result column="name" property="name" />
    <result column="pwd" property="password" />
</resultMap>

<select id="getUserById" parameterType="int" resultMap="userMap">
    SELECT * from mybatis.user where id = #{id}
</select>
~~~





## 日志

Mybatis 通过使用内置的日志工厂提供日志功能。内置日志工厂将会把日志工作委托给下面的实现之一：

- SLF4J
- Apache Commons Logging
- Log4j 2
- Log4j
- JDK logging





log4j

* 1.导入maven

  ~~~ 
  <!-- https://mvnrepository.com/artifact/log4j/log4j -->
  <dependency>
      <groupId>log4j</groupId>
      <artifactId>log4j</artifactId>
      <version>1.2.17</version>
  </dependency>
  ~~~

* 2.配置mybatis-config.xml

  ~~~ xml
   <settings>
      <setting name="logImpl" value="LOG4J"/>
    </settings>
  ~~~

* 添加log4j.properties文件

  ~~~ properties
  # 全局日志配置
  log4j.rootLogger=ERROR, stdout
  # MyBatis 日志配置
  log4j.logger.org.mybatis.example.BlogMapper=TRACE
  # 控制台输出
  log4j.appender.stdout=org.apache.log4j.ConsoleAppender
  log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
  log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n
  ~~~

* 测试

  ~~~ java
  public class UserMapperTest {
      static Logger logger = Logger.getLogger(UserMapperTest.class);
      @Test
      public void getUserList() {
          SqlSession sqlSession = MybatisUtils.getSqlSession();
          logger.info("test log info");
          logger.warn("test log warn");
          logger.error("test log error");
          sqlSession.close();
      }
  }
  ~~~


## 分页

#### 1.使用limit分页

* 接口

  ~~~ java
  List<User> getUserByLimit(Map<String, Integer> map);
  ~~~

* Mapper.xml

  ~~~ java
  <!--//分页-->
      <select id="getUserByLimit" parameterType="map" resultType="usr" >
      select * from mybatis.user limit #{startIndex},#{pageSize}
  </select>
  ~~~

* 测试

  ~~~ java
  @Test
  public void getUserByLimit() {
      SqlSession sqlSession = MybatisUtils.getSqlSession();
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      Map<String, Integer> map = new HashMap<>();
      map.put("startIndex",0);
      map.put("pageSize",2);
      List<User> userLists = userMapper.getUserByLimit(map);
      for (User user : userLists) {
          // logger.info(user.toString());
          System.out.println(user.toString());
      }
      sqlSession.close();
  
  }
  ~~~

### 使用注解开发

对于像 BlogMapper 这样的映射器类来说，还有另一种方法来完成语句映射。 它们映射的语句可以不用 XML 来配置，而可以使用 Java 注解来配置。比如，上面的 XML 示例可以被替换成如下的配置：

```java
package org.mybatis.example;
public interface BlogMapper {
  @Select("SELECT * FROM blog WHERE id = #{id}")
  Blog selectBlog(int id);
}
```

使用注解来映射简单语句会使代码显得更加简洁，但对于稍微复杂一点的语句，Java 注解不仅力不从心，还会让你本就复杂的 SQL 语句更加混乱不堪。 因此，如果你需要做一些很复杂的操作，最好用 XML 来映射语句。

选择何种方式来配置映射，以及认为是否应该要统一映射语句定义的形式，完全取决于你和你的团队。 换句话说，永远不要拘泥于一种方式，你可以很轻松的在基于注解和 XML 的语句映射方式间自由移植和切换。