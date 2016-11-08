> 学以致用,实践出真知,都说明了知识只有被使用并解决问题了才算是有价值的,所以学习跟实践一定要结合起来,为了学习计算机网络,为了学习Linux知识,我决定从工作生活中的一个小问题开始--翻墙.

&emsp;家里有两个路由器,一个小米青春版,一个斐讯K2,都被我刷成了openwrt,深深感到开源的魅力真大,我所学习的计算机知识大部分来自开源社区,感恩.

#### 更换opkg源
&emsp;这一次安装的openwrt版本为CHAOS CALMER (15.05.1),但是官方默认的软件源为SourceForge,国内体验很差,所以更换成国内的镜像,目前找到了两个大学提供的镜像,清华大学和中科大,我选择的是中科大,注释默认的并加入新的:

```
root@OpenWrt:/etc# cat opkg.conf
dest root /
dest ram /tmp
lists_dir ext /var/opkg-lists
option overlay_root /overlay
option check_signature 1
#src/gz openwrt_dist http://openwrt-dist.sourceforge.net/dist/base/ramips
#src/gz openwrt_dist_luci http://openwrt-dist.sourceforge.net/dist/luci

src/gz base http://openwrt.proxy.ustclug.org/chaos_calmer/15.05.1/ramips/mt7620/packages/base
src/gz luci http://openwrt.proxy.ustclug.org/chaos_calmer/15.05.1/ramips/mt7620/packages/luci
src/gz management http://openwrt.proxy.ustclug.org/chaos_calmer/15.05.1/ramips/mt7620/packages/management
src/gz packages http://openwrt.proxy.ustclug.org/chaos_calmer/15.05.1/ramips/mt7620/packages/packages
src/gz routing http://openwrt.proxy.ustclug.org/chaos_calmer/15.05.1/ramips/mt7620/packages/routing
src/gz telephony http://openwrt.proxy.ustclug.org/chaos_calmer/15.05.1/ramips/mt7620/packages/telephony

```

可以通过查看`/proc/cpuinfo`来确定cpu类型和架构

#### 安装shadowsocks系列软件

&emsp;从`openwrt-dist`下载需要的软件到本机电脑然后scp到路由器/tmp目录,然后安装即可:

```
opkg install shadowsocks*.ipk
```

openwrt自带的dnsmasq软件不具有ipset功能,先卸载自带的然后从opkg仓库安装:

```
opkg remove dnsmasq
opkg install dnsmasq-full
```


#### 总结体会
* 尽量使用命令行,如果不必要尽量不使用图形界面.

#### 参考链接
[openwrt-dist](http://openwrt-dist.sourceforge.net/)

[清华大学开源软件镜像站](https://mirrors.tuna.tsinghua.edu.cn/)

[中科大开源软件镜像站](https://mirrors.ustc.edu.cn/)


