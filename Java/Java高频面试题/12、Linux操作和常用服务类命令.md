**面试题：**

Linux 操作和常用服务类命令？

**Linux 简单理解：**

Linux 是一个长时间运行比较稳定的操作系统，一般作为服务器使用，如 Apache、Web、DB 等。

Linux 本身就适合 C语言编译环境，一些没有软件包的程序，需要在 Linux 下手动编译得到软件包。

**Linux 的连接：**

Linux 服务器需要安装 SSH 服务端软件（默认端口号为 22），SFTP服务端软件（默认端口号为 25），使用客户端软件来连接服务器、上传和下载文件，注意 Linux 服务器要启动这两个服务。

常用的客户端软件 Putty + WinSCP，Xshell + Xftp，Xmanager。

**常用的基本命令：**

- pwd 获取当前路径
- cd 切换目录
- su -u 切换到管理员
- ls 列举目录
- tail 查看文件
- rm -rf 删除文件
- vi 编辑文件
- mkdir 创建文件夹
- rm -r 删除文件夹

**常用的服务命令：**

**CentOS 6 下：**

service 命令

- 注册在系统中的标准化程序
- 方便统一的管理方式
  - service 服务名 start
  - service 服务名 stop
  - service 服务名 restart
  - service 服务名 reload
  - service 服务名 status
- 查看服务的方法 /etc/init.d/服务名
- 通过 chkconfig 命令设置自启动
  - 查看服务 chkconfig --list | grep xxx
  - chkconfig --level 5 服务名 on

运行级别：

开机 --》 BIOS --》 /boot --》init进程 --》运行级别 --》运行级对应的服务

查看默认级别：vi /etc/inittab

Linux 系统有 7 种运行级别（常用的是级别 3 和 5）：

- 运行级别 0：系统停机状态，系统默认运行级别不能设为 0，否则不能正常启动
- 运行级别 1：单用户工作状态，root 权限，用于系统维护，禁止远程登录
- 运行级别 2：多用户状态（没有 NFS），不支持网络
- 运行级别 3：完全的多用户状态（有 NFS），登录后进入控制台命令行模式
- 运行级别 4：系统未使用，保留
- 运行级别 5：X11 控制台，登陆后进入图形 GUI 模式
- 运行级别 6：系统正常关闭并重启，默认运行级别不能设为 6，否则不能正常启动

**CentOS 7下：**

使用 systemctl 命令

- 注册在系统中的标准化程序
- 方便统一的管理方式
  - systemctl start 服务名（xxx.service）
  - systemctl stop 服务名（xxx.service）
  - systemctl restart 服务名（xxx.service）
  - systemctl reload 服务名（xxx.service）
  - systemctl status 服务名（xxx.service）

- 查看服务的方法 /usr/lib/systemd/system
- 查看服务的命令
  - systemctl list-unit-files
  - systemctl --type service
- 通过 systemctl 命令设置自启动
  - 自启动 systemctl enable 服务名
  - 不自启动 systemctl disable 服务名