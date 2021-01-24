#### 背景介绍

2021年我决定重新学习kubernates, 所以第一步就是手工安装一下, 于是就在Mac笔记本上安装了virtualbox虚拟机来安装几台CentOS, 安装的思路是先安装一个模板机器, 然后通过模板机器来复制出自己想要的Linux服务器


#### 网络需求

* 虚拟机内部的Linux服务器都可以网络通信
* 虚拟机内部的Linux服务器可以跟宿主机Mac进行通信
* 虚拟机内部的Linux服务器都可以上外网
* 虚拟机内部的Linux服务器都有固定的IP地址


#### 解决方法

virtualbox的网络模式有以下几种:

* 桥接

这种情况可以让虚拟机中的Linux服务器跟Mac电脑平行, 都是连接的公司的网络, 但是公司的网络是需要认证的, 所以这种方式不可行

* 仅主机(Host Only)网络

这种方式可以让虚拟机内部的Linux之间进行通信并且支持跟宿主机也可以进行通信, 并且配置一下就可以实现静态IP

采用这种方式需要先在VirtualBox上创建一个虚拟网络, 稍后配置虚拟机网络的时候选择这个就行

![image](https://user-images.githubusercontent.com/7486508/105624196-c0399980-5e5a-11eb-9479-fe94d53563e6.png)
![image](https://user-images.githubusercontent.com/7486508/105624211-de9f9500-5e5a-11eb-9cfb-616cc0001be1.png)


* 网络地址转换(NAT)

这种方式可以让虚拟机内部的Linux访问公网





第一步, 关闭主机然后去设置主机的网络配置, 启用两块网卡, 网卡一采用`仅主机(Host Only)`模式, 网卡二采用`网络地址转换(NAT)`

第二步, 登陆进去主机查看网络设置:

![image](https://user-images.githubusercontent.com/7486508/105624346-05120000-5e5c-11eb-8ccf-74ecb0cde50b.png)


可以看到第二个网卡就是我们(仅主机 Host Only)创建的IP地址, 默认是DHCP模式的, 我们去修改一下配置文件, 改成固定的IP

![image](https://user-images.githubusercontent.com/7486508/105624398-54f0c700-5e5c-11eb-92c3-afd856908ffa.png)

![image](https://user-images.githubusercontent.com/7486508/105624408-6e920e80-5e5c-11eb-8fea-3b9cd2007f1d.png)

按照上图修改, 即可, 把DHCP改成static, 然后写上静态IP保存, 然后执行`service network restart`即可
