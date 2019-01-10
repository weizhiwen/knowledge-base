**面试题：**

Spring Bean 的作用域之间有什么区别？

**参考答案：**

在 Spring 中，可以在 \<bean\> 元素的 scope 属性里设置 bean 的作用域，来决定这个 bean 是单实例还是多实例的。Spring Bean 的作用域有 singleton、prototype、request、session 四种，后两种仅对 Java Web 环境有效。

|   类别    |                             说明                             |
| :-------: | :----------------------------------------------------------: |
| singleton | 在 Spring IOC 容器中仅存在一个 Bean 实例，Bean 以单例的方式存在。 |
| prototype |         每次调用 getBean() 方法都会返回一个新的实例          |
|  request  | 每次 HTTP 请求都会创建一个新的 Bean，该作用域仅适用于 WebApplicationContext 环境 |
|  session  | 同一个 HTTP Session 共享一个 Bean，不同的 HTTP Session 使用不同的 Bean，该作用域仅适用于 WebApplicationContext 环境 |

