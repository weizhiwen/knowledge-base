from redis import *

# 创建StrictRedis对象和Redis服务器建立连接，这里的IP地址是我本地开的虚拟机IP地址
sr = StrictRedis(host='192.168.1.200')
result = sr.set('name', 'wzw')
print(result)
result = sr.get('name')
print(result)
# 获取键列表
result = sr.keys()
print(result)