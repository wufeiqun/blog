## 背景介绍

&emsp;事情是这样的,有一次我收到一条报警短信,一看是微店的主服务挂了,于是就马上通知了后台的同学,经过排查是从数据库服务器宕机了,ping不通了,联系了托管机房的人确认了此事后就重启了服务器,最终服务好了,服务中断时间大概1小时左右,已经很长了,这不是重点,重点是第二天又TMD宕机了,而且还是同一个服务器,当时机房的人看到了硬件内存报警,后来换了后就最近一段时间没有出现宕机,当然服务还是全部挂到了主数据库上,意思目前是单点,经过这次事件后,我花了一周左右的时间研究了mysql的高可用架构,在这里总结一下.

## 目前的架构

&emsp;不同的架构适应不通的应用场景,好近项目的数据库集群是我从无到有选型并搭建的,由于项目开始的比较突然,当时就使用了最基本的主从架构,主负责写入,从负责读取,读写分离是通过不通账号的授权实现的,这里做一下反思,自己当时安装数据库的时候想的不周到,一般的主从是不具有高可用的,当然从数据库挂了后可以手工改到主数据库上读写,幸亏这次宕机的是从数据库,如果是主数据库的话说不定2个小时也不一定能恢复呢,因为还得更改授权,还得修改后台配置,主数据库回复后还得考虑重新做主从,线上数据库,一点点的操作都需谨慎,因为数据是一切的基础,应该从一开始就设计易扩展的数据库架构,不然到后来数据量大了后就不好迁移了,这次给了我深深的经验教训,于是就花了一些时间去网上搜索了一些更适合的方案.

调研的目的:
 * 寻找一种更适合现有业务的mysql高可用架构方案,保证任何一台数据库(主/从)挂了后不会中断服务,数据丢失到最少.

调研的几个原则:

 * **稳定压倒一切** 数据对于公司的重要性不言而喻,所以选择的这个高可用方案一定的是经过很多中等以上公司的验证的,比如BAT,美团,小米,新浪等.
 * 线上版本尽量统一,目前线上统一使用mysql社区版本5.5.18,所有的方案最好基于这个大版本,除非更高的版本有非常关键的更新.
 * 中间件尽量的少,逻辑尽量简洁明了,中间件越多越不稳定越不易维护.
 * 所选择的高可用方案教程文档丰富,学习成本相对较低.


## 优化后的架构

Keepalived+mysql双主

架构图:
![mysql高可用](http://blog.chinaunix.net/attachment/201209/4/20639775_1346748611m0zM.jpg)

架构优点:
 * 通过两个VIP和两套账号可实现读写分离.
 * 通过keepalived的配置可实现高可用,任何一台服务器挂掉后都会自动漂移到另一台机器上.
 * 可以添加多个从库来实现读取的负载均衡,需要LVS的轮询策略,这里只是使用keepalived,LVS在内核中已经安装,只是在keepalived中可以配置,也可以不配置,这里没有配置,因为不需要轮询,只是使用了keepalived得故障转移.
 * 适合业务量不是很大,两台服务器(64G*8CPU)就能搞定的场景.
 * 配置简单.
 
架构缺点:
* 需要自己编写触发切换的脚本
* 当master挂了后可能会有一部分数据未能同步到slave上
* 未知问题,还没有用到我们生产环境
 

实战环境介绍:

| 主机名 | IP | VIP | 系统 | Mysql | Keepalived |
| :------: | :--: | :---: | :----: | :-------: | :---: |
| test01 | 10.1.1.113 | 10.1.1.176(w)<br>10.1.1.177(r) | centos6.5 X64 | 5.5.18 | 1.2.19 |
| test02 | 10.1.1.75 | 10.1.1.176(w)<br> 10.1.1.177(r) | centos6.5 X64 | 5.5.18 | 1.2.19 |

## 安装过程

### 安装配置Keepalived

#### keepalived是什么

&emsp;keepalived主要用作RealServer的健康状态检查以及LoadBalance主机和BackUP主机之间failover的实现.

#### keepalived基本原理

&emsp;keepalived是以VRRP协议为实现基础的,虚拟路由冗余协议,可以认为是实现路由器高可用的协议,即将N台提供相同功能的路由器组成一个路由器组,这个组里面有一个master和一个或者多个backup,master上面有一个对外提供服务的VIP(该路由器所在的局域网内其他机器的默认路由为该vip), master会发组播,当backup收不到vrrp包时就认为master当掉了,这时候就需要根据[vrrp的优先级](http://tools.ietf.org/html/rfc5798#section-5.1)来[选举一个backup当master](http://en.wikipedia.org/wiki/Virtual_Router_Redundancy_Protocol#Elections_of_master_routers),这样就保证路由器的高可用了.

#### keepalived选举/切换策略简介

&emsp;我们都知道keepalived可以实现failover,那么他到底是怎么实现的呢,我给简单介绍一下,在每个节点的每个配置实例中有有一个`priority`,这是集群中选举的关键依据,记住不是`state`这个参数啊(这就是个幌子) 

#### 编译安装

 1. 下载
```
wget http://keepalived.org/software/keepalived-1.2.19.tar.gz
```
 2. 编译(root)
```
tar -zxvf keepalived-1.2.19.tar.gz
cd keepalived-1.2.19
yum install kernel-devel openssl-devel libnl-devel
./configure

Keepalived configuration
------------------------
Keepalived version       :1.2.29        ##version##
Compiler: gcc               ##编译工具##
Compiler flags           :-g -O2    ##参数##
ExtraLib:-lssl -lcrypto -lcrypt     ##扩展库##
Use IPVS Framework       :Yes       ##LVS核心代码框架，不使用LVS可以编译时disable-lvs##
IPVS sync daemon support :Yes       ##IPVS同步进程，是否开启取决于 IPVS FRAMEWORK###
IPVS use libnl           :Yes       ##是否使用libnl库##
Fwmark socket support    :Yes       ##套接字框架##
Use VRRP Framework       :Yes       ##VRRP框架，keepalived的核心进程vrrpd##
Use VRRP VMAC            :Yes       ##VRRP Virtual mac##
SNMP support             :No
SHA1 support             :No
UseDebug flags           :No

make
make install
```
 3. 拷贝
```
默认编译安装完成后是在`/usr/local/etc`目录下的,需要复制到相应的目录中.
cp /usr/local/etc/keepalived/keepalived.conf /etc/
cp /usr/local/etc/rc.d/init.d/keepalived /etc/rc.d/init.d/
cp /usr/local/etc/sysconfig/keepalived /etc/sysconfig/
```

 4. 测试
 ```
可能会出现如下情况:
[root@test7 etc]# service keepalived restart
Stopping keepalived:                                       [FAILED]
Starting keepalived: /bin/bash: keepalived: command not found
                                                           [FAILED]
是因为keepalived的二进制可执行命令脚本在/usr/local/sbin/中,把这个加到root运行keepalived前的环境变量就行,修改/etc/init.d/keepalived 文件在start字段加入export PATH=$PATH:/usr/local/sbin即可

 ```
 5. 配置文件详解(核心关键)
 keepalived只有一个配置文件在/etc/keepalived.conf,里面主要包括这几个配置区域:global_defs,vrrp_script,vrrp_instance和virtual_server.下面分别介绍:
```
#这一块区域主要配置了全局的邮件通知的一些信息.
global_defs {
   notification_email { #故障发生时邮件发送给谁,可以多个
     rocky@gmail.com
     rocky@hotmail.com
   }
   notification_email_from keepalived@other02 #通知邮件从哪个地址发出来
   smtp_server 127.0.0.1 #smtp服务器地址,如果安装并开启了postfix的话,可以填写本机地址.
   smtp_connect_timeout 30 #连接邮件服务器超时时间
   router_id other #标识本节点的字符,通常为hostname,也可自定义,发邮件时会用到,节点之间不一样.
}

# 这一块区域是来配置自定义触发脚本的,脚本可以返回0(成功)或者1(失败),keepalived通过返回的结果来动态修改本节点的priority的值.一会儿下面会详细介绍,这里可以配置多个脚本.weight为正时,脚本检测成功时,也就是返回0时weight会加到priority上,否则不加;当weight为负时,脚本返回成功(0)则不加,返回失败(1),则原priority会加上weight,也就是减去weight的绝对值.
vrrp_script check_mysql { #check_mysql是脚本的实例名称,下面会用到.
    script "/etc/keepalived/check_mysql.sh" #脚本的具体位置,记住别忘了赋予可执行权限哈~
    interval 5 #每5秒检查一次
    fall 2 #需要2次失败
    rise 3 #需要3次成功
    weight 20 #不是每次都加,只是加一次,意思是priority只可能是两个值,本身或者加上weight后的值.
}


#这里定义虚拟IP的实例和一些属性的,可以是多个当然也可以多个实例加到一个组里面,这样方便组里的每一个实例出问题这个组就出问题,组内每个实例都发生切换,有些时候还是挺有用的,我这里暂时没有用到.
vrrp_instance read { #read是实例名称,仅仅名称而已.
    state BACKUP # 可选参数为MASTER/BACKUP,一般来说是没用的,因为选举是看priority的,但是当设置MASTER的非抢占模式的时候,这里必须为BACKUP(因为决定抢占成为MASTER与否的是BACKUP节点),个人觉得设置成抢占模式会更缩短服务不可用时间,当然网上也有很多人倾向于手工设置而不用抢占模式.
    interface eth0 #监听的网卡,一定要写对,通过这个网卡来监听MASTER发来的广播.
    virtual_router_id 51 #取值在0-255之间,用来区分不同instance的VRRP广播,不同节点相同VIP的这个值必须配置相同,同一个节点的不同instance必须配置的不同.
    nopreempt #当master挂了时,backup接管了,master再次起来时默认会抢占master,设置这个参数时,master起来后就不会去抢占了,因为切换两次对业务的影响更大,如果只有两个节点的时候,感觉抢占模式还是好的,因为如果是非抢占模式,就不好切换回去了.抢占模式必须是所有节点都设置成BACKUP,在目前master那台设置非抢占模式.
    priority 60 # 优先级值,范围在0-255.这个是触发切换的关键参数,两个节点的优先级值差得绝对值必须小于weight,
    advert_int 1 #MASTER发送VRRP包得时间间隔,即多久进行一次master选举(健康检查的时间间隔)
    authentication { # 认证区域
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress { #VIP区域,一般设置为跟本机所在同一个网段,不指定子网掩码默认跟主机是一致的,设置前需要确认这个地址没有在使用啊~
    10.1.1.177  
    }

    track_script { # 这是执行自定义脚本的区域
        check_mysql
        }
}

vrrp_instance write {
    state BACKUP
    interface eth0
    virtual_router_id 41
    priority 50
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
    10.1.1.176
    }
    notify_master /etc/keepalived/to_master.sh #当发生切换本节点成为master时,就会执行这个脚本,重启keepalived时也会执行.即使不发生切换.
    notify_backup /etc/keepalived/to_backup.sh #当发生切换本节点成为backup时,就会执行这个脚本,每个instance都可以定义自己的脚本.
    notify_fault /etc/keepalived/to_fault.sh #当本节点出问题,既不是master也不是backup时候执行这个脚本.
}

备注:两个节点的配置文件不通,注意priority的值,LVS相关配置这里没有用到,也就没有说明了.
```
 6. 测试安装配置的结果
使用局域网中的一台服务器ping VIP read,默认read是在test02上的,把test02关闭或者关闭keepalived程序,看看ping会不会闪断一下后就又恢复了.
```
64 bytes from 10.1.1.177: icmp_seq=21 ttl=62 time=2.476 ms
64 bytes from 10.1.1.177: icmp_seq=22 ttl=62 time=4.044 ms
64 bytes from 10.1.1.177: icmp_seq=23 ttl=62 time=4.667 ms
Request timeout for icmp_seq 24
Request timeout for icmp_seq 25
64 bytes from 10.1.1.177: icmp_seq=26 ttl=62 time=5.434 ms
64 bytes from 10.1.1.177: icmp_seq=27 ttl=62 time=1.844 ms
```
 7. 书写触发脚本和通知脚本
 ```
#check_mysql.sh
#!/bin/bash
check_port(){
    echo "Checking mysql port..."
    netstat -antp|grep LISTEN|grep "\b$1\b"
}
if check_port 3306
then
    echo "mysql is OK"
    exit 0
else
    echo "Mysql Down"
    exit 1
fi

#to_master.sh
#!/bin/bash
Date=$(date +%F" "%T)
echo "I'm test9 now i become master,Please check it~"| mail -s "Mysql Master-Master info" wufeiqun_cn@126.com

# 其他的脚本类似
有几点需要注意:
1.通知邮件使用系统postfix的话必须开启postfix,最好设定开机启动.
2.keepalived的日志使用系统的rsyslog程序,最好也开机启动.chkconfig rsyslog on.
 ```
 到这里,keepalived的配置就结束了,下一步配置mysql的双主复制.

### mysql双主复制

 1. 安装mysql程序,已经有自动化安装脚本
```
https://github.com/hellorocky/common-script/blob/master/shellscript/mysql_install.sh
```
 2. 配置mysql的主从
```
如果第一次安装的话就直接接着走就行了,如果不是第一次安装,就得先备份恢复数据,然后往下走.

设置专门复制的账户,在test01上执行:
grant replication slave,replication client on *.* to repl@'10.1.1.75' identified by "123456";

先看一下test01的`show master status;`
mysql> show master status;
+------------------+----------+--------------+------------------+
| File             | Position | Binlog_Do_DB | Binlog_Ignore_DB |
+------------------+----------+--------------+------------------+
| mysql-bin.000135 |      107 |              | mysql            |
+------------------+----------+--------------+------------------+
1 row in set (0.00 sec)

在test02上执行:
change master to master_host='10.1.1.113',master_port=3306,master_user='repl',master_password='123456',master_log_file='mysql-bin.000135',master_log_pos=107,master_connect_retry=100; 

然后再test02上看看同步的状态,'show slave status\G;':


mysql> show slave status\G;
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: 172.100.102.164
                  Master_User: repl
                  Master_Port: 3306
                Connect_Retry: 60
              Master_Log_File: mysql-bin.000092
          Read_Master_Log_Pos: 107
               Relay_Log_File: test9-relay-bin.000142
                Relay_Log_Pos: 253
        Relay_Master_Log_File: mysql-bin.000092
             Slave_IO_Running: Yes  # yes状态则为正常
            Slave_SQL_Running: Yes  # yes状态则为正常
              Replicate_Do_DB:
          Replicate_Ignore_DB:
           Replicate_Do_Table:
       Replicate_Ignore_Table:
      Replicate_Wild_Do_Table:
  Replicate_Wild_Ignore_Table: mysql.%,rocky.%
        Seconds_Behind_Master: 0 #同步延迟状态
```
 3. mysql的配置文件已经放到github了.
```
https://github.com/hellorocky/common-script/tree/master/configuration
```
 4. mysql双主的测试
```
#!/usr/bin/env python
from flask import Flask, request
import MySQLdb

app = Flask(__name__)
db = MySQLdb.connect('172.100.102.171', 'rocky', '123456', 'test')
cursor = db.cursor()
@app.route('/')
def index():
    return 'Hello,Rocky!!!'

@app.route('/test', methods = ['POST'])
def test():
    name = request.form['name']
    sexy = request.form['sexy']
    age = request.form['age']
    print name,sexy,age
    try:
        cursor.execute("insert into info(name,sexy,age) values('rocky', 'man', 26)")
        db.commit()
        print "Successfully insert into database !!! name is %s !" % name
        return  "Successfully insert into database !!! name is %s !" % name
    except:
        print 'some thing wrong~'
        return 'NO'
        sys.exit(1)


##client.py

#!/usr/bin/env python
import requests

url = 'http://127.0.0.1:8008/test'
while 1:
    try:
        requests.post(url, data = {'name': 'rocky', 'sexy': 'man', 'age': 25})
    except:
        print 'Something wrong~'

测试过程中,主
```
## 常见问题

 1. 这里只是介绍了大致的思路,写完这篇博客我花了很大一部分时间在理解主从复制和failover的测试
 2. 这种方式比起普通的主从架构要好一些,可以实现自动故障转移,但是由于主从复制的原理,总会有一些数据会丢失的,而且配置文件必须跳过1062主键冲突的错误,否则任何一次主的切换会导致数据不一致,复制失败.举个例子,已经在master上写了10条数据(自增ID),这时候突然master挂了,客户端马上往从上写,但同步如果没有完成的话,客户端往从上写的时候不是从11开始的而是从8或者9开始写的,但这时候如果主又好了的话之前主上的8,9开始向slave同步,slave的8,9又向master同步,这就会造成主从上都出现主键冲突了.
## 参考链接
[飞鸿无痕](http://blog.chinaunix.net/uid-20639775-id-3337471.html)
[chenzhiwei](https://github.com/chenzhiwei/linux/tree/master/keepalived)
[主键冲突](http://navyaijm.blog.51cto.com/4647068/1241728)
