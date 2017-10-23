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

#### 具体请求报文

DNS只有两种报文：查询报文、回答报文，两者有着相同格式，这个报文由12字节长的首部和4个长度可变的字段组成 。如下：

![dns_header](https://raw.githubusercontent.com/hellorocky/blog/master/picture/11.dns_header.png)

![2](https://raw.githubusercontent.com/hellorocky/blog/master/picture/12.dns_header_marked.png)

![3](https://raw.githubusercontent.com/hellorocky/blog/master/picture/13.DNS_Header_flag.png)

请求报文中一般包括DNS请求头部和问题区域,下面说说首部区域:

1. 标识字段由客户程序设置,并由服务器返回结果,客户端请求报文和服务端返回的报文的该字段的值是相同的,客户端通过它来确定响应与查询是否是同一个.
2. 16bit的标志字段被划分为若干子字段,如上图3,下面依次介绍:
 * QR字段: 0表示查询报文, 1表示响应报文
 * opcode字段: 通常值为0(标准查询), 1(反向查询), 2(服务器状态请求)
 * AA字段表示`授权回答(authoritative answer)`,用于响应头部,如果为1,则表示该DNS服务器就是查询域名的官方NameServer,如果为0则不是.
 * TC字段表示是否可截断,使用UDP时,1表示截断,0表示没有截断,如果响应的总长度超过512字节时,只返回前512字节
 * RD表示`期望递归(recursion desired)`,1代表使用递归的方式,告诉DNS服务器必须处理这个查询,如果DNS服务器没有就往上游抛,如果为0的话,表示告诉DNS服务器使用迭代的方式查询,如果DNS服务器不是该域名的授权服务器,那么DNS服务器就返回一个能解答该查询的其它名称服务器.一般如果是客户端比如PC,手机等的话使用递归的请求方式,如果是本地DNS,比如114DNS,向上游查询的话一般使用迭代的查询方式.
 * RA只能在响应报文中置为1，表示可以得到递归响应。大多数名字服务器都提供递归查询，除了某些根服务器。
 * 随后的3个bit必须为0
 * rcode是一个返回码字段,通常为0(没有差错)和3(名字差错)

 对于查询报文，问题(question)数通常是1，而其他3项则均为0。类似地，对于应答报文，回答数至少是1，剩下的两项可以是0或非0。
 
 下面说说请求时的问题部分:
 
 ![](https://raw.githubusercontent.com/hellorocky/blog/master/picture/14.DNS_question.png)
 
 ![](https://raw.githubusercontent.com/hellorocky/blog/master/picture/14.DNS_wireshark_qustion.png)
 
 问题部分又细分为3个字部分:
 
 1. 查询名表示查询的域名,表示方法为不算`.`每一段的长度后紧跟该段的内容,然后以`0`为结束标志;以`www.google.com`为例:看上图下面的十六进制数字,`03`表示`www`长度,`77 77 77`表示`www`,`06`表示`google`的长度,`67 6f 6f 67 6c 65`表示`google`,`03`表示`com`的长度,`63 6f 6d`表示`com`,`00`表示结束标志;
 2. 后面紧接着的四个字节,前一组`00 01`表示查询类型为`A`查询
 3. 后面一组`00 01`表示查询类,通常为1,指互联网地址


#### 响应报文

&emsp;DNS报文的后面3个字段: 回答字段, 授权字段, 和附加信息字段一般出现在响应报文中,均采用一种称为资源记录值RR(Resource Record)的相同格式,如下图:

![](https://raw.githubusercontent.com/hellorocky/blog/master/picture/15.RR.png)

![](https://raw.githubusercontent.com/hellorocky/blog/master/picture/16.DNS_resp.png)

* 域名就是要查询的域名
* 类型为`A`,说明要查询的是IPv4地址
* 类通常为1,表明是互联网
* 生存时间是客户端程序保留该资源记录的秒数,资源记录通常为2天,这里为471秒
* 资源数据长度,如果为IPv4的话就是4个字节
* 资源数据,这就是客户端要的IPv4地址了


到此一个完整的DNS请求/响应完成了,

#### 参考文档

[结合wireshark学习DNS协议](http://blog.csdn.net/hunanchenxingyu/article/details/21488291)
