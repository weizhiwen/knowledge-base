MongoDB 安装完后，默认只能本机访问，修改mongod.cfg文件中的 bindIP 为 0.0.0.0 使其他计算机也能访问。Mongo DB 默认不需要验证，可以创建一个管理员用户，[参考文章](https://www.jianshu.com/p/62736bff7e2e)

MonoDB 中一些名词和关系型数据的区别：
|名词|MongoDB|MySQL|
|---|---|---|
|数据库| db | database |
|数据表| collection | table |
|字段| field | colum |
|一条数据| document | row |

查看 Mongo Shell 支持哪些命令 `help`
*相当于 MySQL Shell 的 `help` 或者 `?` 命令。*

显示当前所有的数据库列表 `show dbs`
*相当于 MySQL Shell 的 `show databases;`*

显示当前数据库中的数据表 `show collections`
*相当于 MySQL Shell 的 `show tables;`*

查看当前在哪个数据库下面（如果没有显式切换到某个数据库，默认为 test 数据库） `db`
*相当于 MySQL Shell 的 `select database();`*

插入一条文档，db.collection.insertOne({})
*相当于 MySQL Shell 的 `INSERT INTO table() VALUES()`*
插入多条文档，db.collection.insertAll([{}, {}])
*相当于 MySQL Shell 的 `INSERT INTO table() VALUES(), ()`*
*注：在 Mongo Shell 中插入整数会被当作是 64 位的浮点数类型。*

更新一条文档部分数据（条件在前，更改在后），db.collection.updateOne({update operator: {}})
*相当于 MySQL Shell 的 `UPDATE table SET ... WHERE ...`*
更新多条文档部分数据（条件在前，更改在后），db.collection.updateMany({update operator: {}})
更新一条文档除 \_id 外的所有数据，db.collection.replaceOne({update operator: {}}, {})
*注：updateMany()可以多条文档，更新的条件适合多条文档。而 updateOne() 方法更新条件即使是对于多条文档的，也只能更新第一条匹配的文档。replaceOne() 方法适合更新整条文档（ObjectId 一旦设置就无法修改）*

查询一条文档，db.collection.findOne({query, projection})
*相当于 MySQL Shell 的 `SELECT ... FROM table WHERE ...`*
查询多条文档，db.collection.find({query, projection})
*相当于 MySQL Shell 的 `SELECT ... FROM table WHERE ...`*
*注：find() 方法可以查询多条文档，而 findOne() 方法即使查询条件是对于多条文档的，也只能查询除第一条匹配的文档。如果只查询部分字段，可以在 projection 中指定值，1/True 都表示不查询，0/False 都表示查询，但 \_id 默认是返回的，\_id: 0 才是不返回。*

删除一条文档，db.collection.deleteOne({})
*相当于 MySQL Shell 的 `DELETE FORM table WHERE ...`*
删除多条文档，db.collection.deleteMany({})
*相当于 MySQL Shell 的 `DELETE FORM table WHERE ...`*
