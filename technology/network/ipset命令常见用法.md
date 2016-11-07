&emsp;最近在搞openwrt的iptables的时候遇到了一个添加规则的方法中使用了`ipset`,其中用到了一个命令ipset,不是很明白,但是感觉很吊,就专门研究了一下,记录一下初步研究,后续根据经验不断地增加实战经验总结.

#### 简介
> IP sets are a framework inside the Linux kernel, which can be administered by the ipset utility. Depending on the type, an IP set may store IP addresses, networks, (TCP/UDP) port numbers, MAC addresses, interface names or combinations of them in a way, which ensures lightning speed when matching an entry against a set.

&emsp;`ipset`是Linux防火墙iptables的一个伴随工具.ipset 提供了把这个 O(n) 的操作变成 O(1) 的方法：就是把要处理的 IP 放进一个集合，对这个集合设置一条 iptables 规则。像 iptable 一样，IP sets 是 Linux 内核中的东西，ipset 这个命令是对它进行操作的一个工具。一般使用场景是对于大量的IP地址创建规则的时候,比如翻墙的时候遇到中国的IP就走国内的路线.

#### 简单使用
咱们以屏蔽一组IP地址来举例说明吧,回头再加上openwrt的使用场景.这里使用的是一个内网的测试机,所在网段为`172.100.102.0/24`,我的笔记本所在的网段为`172.100.108.0/24`.

* 先创建一个新的网络地址的“集合”。下面的命令创建了一个新的叫做“myset”的“net”网络地址的“hash”集合:
```
#ipset create myset hash:net
```

* 把你希望屏蔽的IP地址添加到集合中,如果有很多的话可以写一个shell的循环加入到创建的ipset中.
```
#ipset add myset 172.100.108.0/24
```

* 创建`iptables`规则屏蔽笔记本所在网段的请求,也就是`DROP`所有笔记本所在网段请求来的数据包.
```
#iptables -I INPUT -m set --match-set myset src -j DROP
```
* 经测试,在笔记本上`ping/telnet/ssh`都会提示`timeout`

* 查看所有已经创建的`ipset`
```
#ipset list
```

* 删除指定的`ipset`(不加参数默认删除所有已经创建的ipset)
```
#ipset destroy myset
```

#### 注意事项
* `ipset/iptables`命令行所添加的规则是保存在内存中的,重启及其以后所有的规则将会失效,所以持久化或者开机启动要根据不同平台自己来实现

#### 引用
[ipset man page](https://linux.die.net/man/8/ipset)
[official site](http://ipset.netfilter.org/index.html)



