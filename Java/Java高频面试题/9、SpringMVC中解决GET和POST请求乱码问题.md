**面试题：**

SpringMVC 中如何解决 GET 请求中文乱码问题，POST 请求方式又如何处理呢？

**GET 请求中文乱码：**

在 Tomcat 服务器的 **sever.xml** 文件中配置 URI 的编码，如下面的配置 `URIEncoding="UTF-8"` 是关键。

``` xml
<Connector port="8080" protocol="HTTP/1.1" connectionTimeout="20000"
               redirectPort="8443" URIEncoding="UTF-8"/>
```

**POST 请求中文乱码：**

在 web.xml 中配置编码过滤器，设置 CharacterEncodingFilter 类的 encoding 属性为 UTF-8，forceRequestEncoding 和 forceResponseEncoding 的值（默认初始化为 false）也可以设为 true，如下配置。

```xml
<!--SpringMVC 编码过滤器-->
<filter>
    <filter-name>characterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
    <init-param>
        <param-name>forceRequestEncoding</param-name>
        <param-value>true</param-value>
    </init-param>
    <init-param>
        <param-name>forceResponseEncoding</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

