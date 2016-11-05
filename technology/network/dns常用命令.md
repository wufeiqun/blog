* dig
```
dig命令是一个命令行域名解析工具,简单强大.

简单使用: dig @server name type

server  -指的是DNS服务器IP地址,比如8.8.8.8,如果没有指定的话就会去查询/etc/resolv.conf文件
name    -要解析的域名
type    -查询类型,默认为A记录,也是最常用的

dig @8.8.8.8 baidu.com A


指定端口: dig -p port @server name

#比如调试chinadns的时候,会在路由器上监听udp5353端口
dig -p 5353 @192.168.1.1 baidu.com


指定文件: dig -f filename @server
文件中为域名列表,每一行一个,依次查询文件中的域名


使用TCP代替UDP解析
dig +tcp @8.8.8.8 jp.rockywu.me


追踪dig全过程

dig +trace baidu.com
dig会从根域查询一直跟踪到最终的结果并将整个信息输出


简单输出
dig +short jp.rockywu.me
118.193.81.214




```
