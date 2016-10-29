#### 背景介绍

> 最近收集了一些电子书放到了github,但是pdf文件太大了,家里网不给力,连接github太慢,所以试了试[proxychains-ng](https://github.com/rofl0r/proxychains-ng)

#### 安装

```
brew install proxychains-ng
```

#### 配置

```
配置文件位置为: /usr/local/Cellar/proxychains-ng/4.11/etc/proxychains.conf

只需要在最后一行加上本地socks5代理的地址即可,比如socks5 127.0.0.1 1080,这是从shadowsocks的配置文件里配置的.
```

#### 使用方法

```
(pyenv) rocky@homemac  ~  proxychains4 curl ip.cn
[proxychains] config file found: /usr/local/Cellar/proxychains-ng/4.11/etc/proxychains.conf
[proxychains] preloading /usr/local/Cellar/proxychains-ng/4.11/lib/libproxychains4.dylib
[proxychains] DLL init: proxychains-ng 4.11
[proxychains] Strict chain  ...  127.0.0.1:1080  ...  ip.cn:80  ...  OK
当前 IP：161.202.82.168 来自：日本 SoftLayer
```

#### 添加快捷方法

```
vim .zshrc
alias pc='proxychains4'
```
