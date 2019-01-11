**面试题：**

Redis 在项目中的使用场景？

| 数据类型 |                           使用场景                           |
| :------: | :----------------------------------------------------------: |
|  String  |            如封锁一个恶意IP地址，使用 incrby 命令            |
|   Hash   | 如存储用户信息 [id, name, age]<br />Hset(key, field, value)<br />Hset(userKey, id, 101)<br />Hset(userKey, name, admin)<br />Hset(userKey, age, 23)<br />——修改案例——<br />Hget(userKey, id)<br />Hset(userKey, id, 102)<br />为什么不使用 String 类型来存储？<br />Set(userKey, 用户信息字符串)<br />Get(userKey)<br />修改时，IO 的负担大，所以不建议使用 String 类型 |
|   List   | 实现最新消息的排行，可以利用 List 的 push 命令，将任务存在 List 集合中，同时使用另一个命令，将任务从集合中取出 pop 命令。<br />Redis-list 数据类型来模拟消息队列，如电商中的秒杀就可以采用这种方式来完成一个秒杀活动。 |
|   Set    | 特殊之处：可以自动排重，如微博中将每个人的好友存在集合（Set）中，求两个人共同好友，只需要求集合的交集即可。 |
|   Zset   | 以某个条件为权重，进行排序。<br />京东：商品列表页都有一个综合排名，还可以按照价格进行排名。 |

