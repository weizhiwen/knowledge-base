**面试题：**

MyBatis中，当实体属性名和表字段名不一致时，怎么办？

**解决方法：**

1、写 SQL 语句时起别名。

数据库使用下划线命名规则，Java 类使用驼峰法命名。

```xml
<select id="selectUserById" parameterType="java.lang.String" resultMap="cn.edu.yangtzeu.entity.User">
    select id, last_name lastName, dept_id deptId
    from user where id = #{id}
</select>
```

2、在 MyBatis 的全局配置文件中开启驼峰命名规则。

```xml
<settings>
    <!--开启驼峰命名法规则，默认是 false-->
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>
```

3、在 Mapper 映射文件中使用 resultMap 自定义映射规则。（最常用，更灵活）

一般都是推荐使用这种方式，使用插件生成的配置文件就是使用的这种方式。

```xml
<resultMap id="BaseResultMap" type="cn.edu.yangtzeu.entity.User">
    <!--主键列用 id 标签，property为对象的属性-->
    <id column="id" jdbcType="CHAR" property="id"/>
    <result column="name" jdbcType="VARCHAR" property="name"/>
    <result column="password" jdbcType="VARCHAR" property="password"/>
</resultMap>
```