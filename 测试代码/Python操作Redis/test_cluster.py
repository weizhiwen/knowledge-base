from rediscluster import *

if __name__ == "__main__":
    try:
        startup_nodes = [
            {'host': '192.168.2.100', 'port': '7000'},
            {'host': '192.168.2.101', 'port': '7003'},
            {'host': '192.168.2.101', 'port': '7004'},
        ]
        src = StrictRedisCluster(startup_nodes=startup_nodes)
        print(src.set('result', 'success'))
        print(src.get('result'))
    except Exception as e:
        print(e)