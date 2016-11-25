#### 方案介绍
&emsp;由于工作和生活中翻墙的场景很多,终端有MacBook,Iphone,Android等等,需要翻墙的软件也有很多,比如浏览器,终端等等,所以传统的每个平台安装一个软件,而且主要依靠URL来判断是否翻墙的感觉很不爽,所以说干就干,折腾了一周的时间终于选定了自己的'完美'翻墙方案👇

* 上网的过程主要有两步:DNS解析和网络连接,这两步分开处理
* 对于DNS解析,采用的策略是国内的网站使用国内的DNS解析,网友的力量很强大,[github](https://github.com/felixonmars/dnsmasq-china-list)不断更新国内的域名列表,其它的域名采用ss-tunnel隧道转发到8.8.8.8解析,或者使用自己VPS搭建的域名,稳定性自己不断测试中
* 真正决定是否翻墙的是IP地址的位置,这个可以从[官方网站](http://ftp.apnic.net/apnic/stats/apnic/delegated-apnic-latest)来获得
