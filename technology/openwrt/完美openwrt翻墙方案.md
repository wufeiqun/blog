#### 方案介绍
&emsp;由于工作和生活中翻墙的场景很多,终端有MacBook,Iphone,Android等等,需要翻墙的软件也有很多,比如浏览器,终端等等,所以传统的每个平台安装一个软件,而且主要依靠URL来判断是否翻墙的感觉很不爽,所以说干就干,折腾了一周的时间终于选定了自己的'完美'翻墙方案👇

* 上网的过程主要有两步:DNS解析和网络连接,这两步分开处理
* 对于DNS解析,采用的策略是国内的网站使用国内的DNS解析,比如114DNS,阿里DNS等,国外的网站包括一些容易被GFW投毒的DNS采用国外自己搭建的纯净DNS解析
* 真正决定是否翻墙的是IP地址的位置,这个可以从[官方网站](http://ftp.apnic.net/apnic/stats/apnic/delegated-apnic-latest)来获得


使用到的软件或者命令:

* dnsmasq --作为DHCP服务器和本地DNS缓存服务器,直接把ChinaDNS作为上游服务器
* ChinaDNS --作为dnsmasq的上游DNS服务器,它的参数需要配置一个国内的DNS和一个国外的纯净DNS
* ss-tunnel --在本地和远程服务器搭建一个隧道用于DNS远程解析来作为ChinaDNS的纯净上游
* ss-redir --用于透明代理,配合iptables使用
* iptables --创建路由规则
* ipset --把国内的IP网段加入到hash数据结构中,让IP的查询的时间复杂度变成O(1) 