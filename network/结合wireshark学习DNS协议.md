#### 背景介绍

&emsp;之前学习`shadowsocks`代码的时候看到里面自己实现了DNS的解析,感觉好高大上,之前一直觉得DNS协议很复杂,看到他的实现,感觉原来不过如此嘛,就一直想自己亲手造一个轮子实现DNS的请求;后来很巧看到一个项目就是自己实现的DNS Server,看了看代码也不复杂,决定花一个周末看看具体实现,这里做了一下记录,结合wireshark来学习DNS协议.

> 深深感到开源世界真好,我所学到的大部分知识都是源于开源项目,真心愿意为开源世界付出一点自己的贡献!

#### DNS简介

&emsp;认识DNS时间也挺长的了,从读大学的时候就知道修改DNS可以让上网更快,那个时候对DNS仅有的认识也就是我发给它域名,它返回给我IP地址,很核心,但是没有了解这一套系统的背后实现,直到现在才稍微懂了一点.

&emsp;其实DNS的出现就是为了解决一个问题,帮助人们记住IP地址,因为TCP/IP通信是IP和IP之间的,但是这些数字人们记不住,所以DNS协议才出现,它记录了域名→IP的映射,这样,人们只需要记住域名就行了.

&emsp;DNS包括很多类型,我们这里主要介绍A记录,也就是域名→IP地址.


#### 一个完整的DNS请求过程

&emsp;DNS系统是Client/Server模式,上网的话首先配置DNS,我们这里配置的是114DNS,大部分的网络工具都带有DNS客户端,比如浏览器,wget,curl等.这里拿`gz.rockywu.me.`这个域名进行解析:

```bash
test@sdzx$ dig +trace @114.114.114.114 gz.rockywu.me.

; <<>> DiG 9.8.3-P1 <<>> +trace @114.114.114.114 gz.rockywu.me.
; (1 server found)
;; global options: +cmd
.			97727	IN	NS	a.root-servers.net.
.			97727	IN	NS	k.root-servers.net.
.			97727	IN	NS	i.root-servers.net.
.			97727	IN	NS	h.root-servers.net.
.			97727	IN	NS	l.root-servers.net.
.			97727	IN	NS	e.root-servers.net.
.			97727	IN	NS	j.root-servers.net.
.			97727	IN	NS	f.root-servers.net.
.			97727	IN	NS	b.root-servers.net.
.			97727	IN	NS	m.root-servers.net.
.			97727	IN	NS	c.root-servers.net.
.			97727	IN	NS	g.root-servers.net.
.			97727	IN	NS	d.root-servers.net.
;; Received 228 bytes from 114.114.114.114#53(114.114.114.114) in 26 ms

me.			172800	IN	NS	a2.me.afilias-nst.info.
me.			172800	IN	NS	c0.cctld.afilias-nst.info.
me.			172800	IN	NS	a0.cctld.afilias-nst.info.
me.			172800	IN	NS	ns.nic.me.
me.			172800	IN	NS	ns2.nic.me.
me.			172800	IN	NS	b2.me.afilias-nst.org.
me.			172800	IN	NS	d0.cctld.afilias-nst.org.
me.			172800	IN	NS	b0.cctld.afilias-nst.org.
;; Received 489 bytes from 192.58.128.30#53(j.root-servers.net) in 74 ms

rockywu.me.		86400	IN	NS	lv3ns1.ffdns.net.
rockywu.me.		86400	IN	NS	lv3ns2.ffdns.net.
rockywu.me.		86400	IN	NS	lv3ns3.ffdns.net.
rockywu.me.		86400	IN	NS	lv3ns4.ffdns.net.
;; Received 124 bytes from 89.188.44.44#53(ns.nic.me) in 199 ms

gz.rockywu.me.		600	IN	A	123.249.94.160
rockywu.me.		3600	IN	NS	lv3ns2.ffdns.net.
rockywu.me.		3600	IN	NS	lv3ns1.ffdns.net.
rockywu.me.		3600	IN	NS	lv3ns4.ffdns.net.
rockywu.me.		3600	IN	NS	lv3ns3.ffdns.net.
;; Received 140 bytes from 112.29.150.45#53(lv3ns3.ffdns.net) in 73 ms

```

&emsp;首先明确两个基本概念,Recursive(递归)和Iterative(迭代),这是两种DNS查询方式,拿DNS解析这件事来说明,如果是纯递归方式的话,流程是这样的: 笔记本请求114DNS,询问gz.rockywu.me的IP-->114DNS不知道结果,向上游接着询问-->上游不知道,接着向自己的上游询问--->直到上游的上游直到具体答案才返回;

&emsp;而纯迭代的方式是这样的: `114DNS`向根域名服务器询问`gz.rockywu.me.`的IP地址,根域说我不知道,但我知道.me服务器直到,所以根域名服务器把`.me`返回给`114DNS`,然后`114DNS`接着向`.me`询问,他不知道,但他知道`rockywu.me`的权威nameserver知道,就把权威名称服务器的地址返回给了`114DNS`,一次下去,直到找到

具体流程:

1. 当想浏览器输入`gz.rockywu.me`的时候,浏览器首先去本地的`hosts`文件里面去查看有没有该域名的记录.如果有的话直接返回并向该IP发送TCP连接;
2. 如果本地`hosts`文件里面没有的话就去查看本地DNS缓存,Mac的话使用`mDNSResponder`这个进程负责.如果有的话直接返回IP地址;
3. 如果本地DNS缓存也没有的话,程序就回去请求本地已经配置好的DNS服务器,比如114DNS,114DNS首先会去查询本地缓存,如果有的话直接返回;
4. 如果`114DNS`本地缓存中没有的话,`114DNS`就会把`gz.rockywu.me`同时发送给13组根域名服务器,13组跟服务器采用抢答模式,回答说我不知道,但是`.me`的`NameServer`知道,我把它的`NameServer`给你,这时`114DNS`采用了最先回答的那个答案,收到了`.me`的`NameServer`地址;
5. `114DNS`向`.me`的`NameServer`请求询问`gz.rockywu.me.`的IP,这里以`ns.nic.me`为例,他说他不知道,但他知道`rockywu.me.`的`NameServer`知道,于是把`NameServer`返回给`114DNS`;
6. `114DNS`向`rockywu.me.`的`NameServer` `lv3ns3.ffdns.net`发送请求,最终得到答案;
7. 其中用户向`114DNS`请求的过程属于递归查询,`114DNS`向上游查询的过程属于迭代查询;

注意事项:

1. 域名后面加点是完全可以正常进行DNS解析的，之所以无法访问这些加点的链接，是因为服务器端做了访问限制或者 rewrite。

#### 具体请求回应报文

DNS只有两种报文：查询报文、回答报文，两者有着相同格式，这个报文由12字节长的首部和4个长度可变的字段组成 。如下：

![dns_header](https://raw.githubusercontent.com/hellorocky/blog/master/picture/11.dns_header.png)

![](https://raw.githubusercontent.com/hellorocky/blog/master/picture/12.dns_header_marked.png)

下面说说首部区域:

1. 标识字段由客户程序设置,并由服务器返回结果,客户端请求报文和服务端返回的报文的该字段的值是相同的,客户端通过它来确定响应与查询是否是同一个.
2. 16bit的标志字段被划分为若干子字段
