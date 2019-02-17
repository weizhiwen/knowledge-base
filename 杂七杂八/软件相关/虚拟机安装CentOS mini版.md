# 虚拟机安装CentOS mini版

## 安装完后需要做的几件事

**1、添加DNS服务器**

`cd /etc/sysconfig/network-scripts/`，修改网络配置文件，如 ifcfg-ens33，在文件末尾追加`DNS=8.8.8.8`，最后重启网络设置，`ifup ens33`，ens33 为网卡名。

**2、安装库**

`yum install -y net-tools` 安装网络相关的命令工具，如 ping 命令，方便后面对网络进行测试。

**3、固定IP地址**

在虚拟机中安装完CentOS，我并不会直接在虚拟机中操作Linux，我喜欢在Xshell等终端工具中操作，因为有时候要同时操作几台Linux，尤其是在集群中。虚拟机连接网络有多种方式，我选用NAT模式，配置静态的IP地址，方便在终端工具中连接。  
参考博客：https://wenshixin.gitee.io/blog/2019/01/15/%E5%9C%A8%E8%99%9A%E6%8B%9F%E6%9C%BA%E4%B8%AD%E9%85%8D%E7%BD%AE%E9%9D%99%E6%80%81IP/
