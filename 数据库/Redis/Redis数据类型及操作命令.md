# Redis 数据类型及操作命令

## 1、数据类型

Redis key（键）的类型只能为字符串，value （值）的类型如下 5 种类型：

- string —— 字符串
- hash —— 无序散列表
- list —— 列表
- set —— 无序集合
- zset —— 有序集合

## 2、操作命令

>说明这里的命令只是 Redis 命令的冰山一角，更多命令的详细介绍可以参看这里 [Redis 中文网的命令中心](http://redis.cn/commands.html)，重要！！！

### 2.1 string

对整个字符串或者字符串的其中一部分执行操作，对整数和浮点数执行自增或者自减操作。

- `set key value` 添加或修改（对已存在的键）单个字符串

``` bash
set name xiaoming
```

- `get key` 获取（查询）键对应的字符串值

``` bash
get name
```

- `del key` 删除键和对应的值

``` bash
del key 
```

- `mset key value [key value ...]` 添加或修改（已存在的键）多个字符串

``` bash
mset user1 xiaoming user2 xiaohong
```

- `mget key [key ...]` 获取（查询）多个键对应的字符串值

``` bash
mget user1 user2
```

- `incr key` 对整数型的字符串加 1

``` bash
set age 21
incr age // 22
get age  // 22
```

- `decr key` 对整数型的字符串减 1

``` bash
set age 22
decr age // 21
get age  // 21
```

- `append key value` 追加值

``` bash
set name aaa
get name // aaa
append name bbb
get name // aaabbb
```

### 2.2 hash

⽤于存储对象，对象的结构为属性、值，添加、获取、移除单个键值对 获取所有键值对 检查某个键是否存在。

- `hset key field value` 设置单个属性

``` bash
hset user name xiaoming
```

- `hget key field` 获取单个属性的值

``` bash
hget user name // xiaoming
```

- `hmset key field1 value1 field2 value2 ...` 设置多个属性

``` bash
hmset user name xiaoming age 21
```

- `hmget key field1 field2 ...` 获取多个属性的值

``` bash
hmget user name age
```

- `hkeys key` 获取指定键所有的属性

``` bash
hkeys user
```

- `hvals key` 获取指定键所有属性的值

``` bash
hvals user
```

- `hdel key field1 [field2 ...]` 删除指定的属性及属性对应的值

``` bash
hdel user name
```

### 2.3 list

列表的元素类型为 string，按照插入顺序排序，从两端压入或者弹出元素，读取单个或者多个元素进行修剪，只保留一个范围内的元素。

- `lpush key value1 [value2 ...]` 从左端插入数据

``` bash
lpush nums1 0 1 2
```

- `rpush key value1 [value2 ...]` 从右端插入数据

``` bash
rpush nums2 0 1 2
```

- `lrange key start stop` 获取键对应列表指定范围的元素，start 为开始索引（索引从左侧开始，第一个元素下标为 0），stop 为结束索引（索引可以是负数，表示从右侧开始）

``` bash
lrange nums1 0 -1 // 2 1 0
lrange nums2 0 -1 // 0 1 2
```

- `linsert key before|after pivot value` 在 pivot 的前或后插入元素

``` bash
linsert key nums1 before 2 3
lrange nums1 0 -1 // 3 2 1 0
```

- `lset key index value` 设置（修改）指定索引位置的元素值

``` bash
lset nums2 1 11
lrange nums2 0 -1 // 0 11 2
```

- `lrem key count value` 删除键对应列表前 count 次出现的值（count > 0 表示从头往尾删除，count < 0 表示从尾往头删除，count = 0 表示删除所有）

``` bash
lpush ab a b a a b a b b
lrange ab 0 -1 // b b a b a a b a
lrem ab 2 b // a b a a b a
```

### 2.4 set

无序集合的元素为 string，元素具有唯一性，不重复，对集合没有修改操作，添加、获取、移除单个元素 检查一个元素是否存在于集合中 计算交集、并集、差集 从集合里面随机获取元素。

- `sadd key member [member ...]` 添加元素到无序集合

``` bash
sadd set1 0 0 1 1 2 2
```

- `smembers key` 查看无序集合中的元素成员

``` bash
smembers set1 // 0 1 2
```

- `scard key` 查看无序集合中的元素数量

``` bash
scard set1 // 3
```

- `srem key member [member ...]`  删除指定无序集合中的元素成员

``` bash
srem set1 1
```

- `sinter key [key ...]` 计算交集

``` bash
sadd set1 0 1 2
sadd set2 1 2 3
sinter set1 set2 // 1 2
```

- `sinterstore destination key [key]` 计算交集并将交集值存储在 destination 集合中

``` bash
sadd set1 0 1 2
sadd set2 1 2 3
sinterstore set1_inter_set2 set1 set2
smembers set1_inter_set2 // 1 2
```

- `sunion key [key ...]` 计算并集

``` bash
sunion set1 set2 // 0 1 2 3
```

- `sunionstore destination key [key]` 计算并集并将并集值存储在 destination 集合中

``` bash
sadd set1 0 1 2
sadd set2 1 2 3
sunionstore set1_union_set2 set1 set2
smembers set1_union_set2 // 0 1 2 3
```

- `sdiff key [key ...]` 计算差集

``` bash
sdiff set1 set2 // 0
sdiff set2 set1 // 3
```

- `sdiffstore destination key [key]` 计算差集并将差集值存储在 destination 集合中

``` bash
sadd set1 0 1 2
sadd set2 1 2 3
sdiffstore set1_diff_set2 set1 set2
sdiffstore set2_diff_set1 set1 set2
smembers set1_diff_set2 // 0
smembers set2_diff_set1 // 3
```

### 2.5 zset

有序集合的元素为 string，元素具有唯一性，不重复，每个元素都会关联一个 double 类型的 score 表示权重，通过权重来给元素排序，没有修改操作，添加、获取、删除元素 根据分值范围或者成员来获取元素 计算一个键的排名。

- zadd key score member [score member ...] 添加元素到有序集合

``` bash
zadd rank 10 xiaoming 9 xiaohong 8 xiaoguang 7 xiaohei 6 xiaozhi 5 xiaozhang
```

- zrange key start stop 查看有序集合中所有元素（默认是升序排序）

``` bash
zrange rank 0 -1 // xiaozhang xiaozhi xiaohei xiaoguang xiaohong xiaoming
```

- `zcard key` 查看有序集合中的元素数量

``` bash
zcard rank // 6
```

- zrangebyscore min max 查看有序集合中 score 在 min 和 max 之间的元素

``` bash
zrangebyscore 6 8 // xiaozhi xiaohei xiaoguang
```

- zscore key member 查看有序集合中指定元素的 score 值

``` bash
zscore rank xiaohei // 7
```

- zrem key member [member ...] 删除有序集合中指定元素及对应的 score 值

``` bash
zrem rank xiaohei xiaozhang
```

- zremrangebyscore key min max 删除有序集合中指定返回 score 值的元素

``` bash
zremrangebyscore rank 7 8
```

### 2.6 key 命令

查找键的相关命令（对上面的类型都适用），参数支持正则表达式。

- `keys pattern` 根据规则查找键

``` bash
keys * // 查找所有的键
keys a* // 查找 a 开头的键
```

- `exists key [key ...]` 判断键是否存在，如果存在返回 1，不存在返回 0

``` bash
exists name
```

- `type key` 查看键对应的 value 类型

``` bash
type name
```

- `del  key [key ...]` 删除键和键对应的值

``` bash
del name
```

- `expire key seconds` 设置键的过期时间，单位为秒

``` bash
set name xiaoming
get name // xiaoming
expire name 1
get name // nil
```

- `ttl key` 查看剩下的有效时间，单位为秒

``` bash
set name xiaoming
get name // xiaoming
expire name 100
ttl name // 98
ttl name // 97 有效时间逐渐减少
```

