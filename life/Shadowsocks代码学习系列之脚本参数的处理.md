title: Shadowsocks代码学习系列之命令行解析工具的总结
date: 2016-01-02 23:29:50
tags: [shadowsocks, 代码学习, python]
commons: true
categories: 编程语言
description: 你知道学习一门语言最快速的办法是什么吗?对,就是看代码,改代码,教别人写代码,打着学习python的旗号,我开始写一系列的关于shadowsocks代码学习的文章,呵呵,废话少说,搞起啊~
---

## 背景介绍

&emsp;前一段时间参加dockerone的活动,领了一个一年多的日本机房的VPS,做点什么呢?呵呵,没错,搭个梯子,去外面的世界看看,有句话说的很对,一个合格的程序员的必备技能就是FQ,看来我还是合格了哈,搭建好了以后自己用了很长时间,感觉爽的不得了,也分享给了不错的朋友,大概10多个人共同使用一个VPS来翻墙,偶然间想到了一个问题,这么多人同时使用一个VPS,一个账号,万一有人泄露了怎么办呢?万一有一天速度变慢又该怎么知道谁用的最多呢?该怎么统计这一切呢?能不能做出一个漂亮的Dashboard呢?很多有趣的问题接踵而来,应接不暇,爽,又有的学习机会了,于是就开始各种纬度地研究SS,于是就有了这一系列的文章了,注解代码在我[github](https://github.com/hellorocky)上呢.

## 命令行解析工具在SS中的使用

&emsp;SS中的命令行解析工具的使用的地方并不是很多,只有在启动服务端/客户端的时候用到了,源代码如下:

```
def get_config(is_local):
    global verbose

    logging.basicConfig(level=logging.INFO,
                        format='%(levelname)-s: %(message)s')
    if is_local:
        shortopts = 'hd:s:b:p:k:l:m:c:t:vq'
        longopts = ['help', 'fast-open', 'pid-file=', 'log-file=', 'user=',
                    'version']
    else:
        shortopts = 'hd:s:p:k:m:c:t:vq'
        longopts = ['help', 'fast-open', 'pid-file=', 'log-file=', 'workers=',
                    'forbidden-ip=', 'user=', 'manager-address=', 'version']
    try:
        config_path = find_config()
        optlist, args = getopt.getopt(sys.argv[1:], shortopts, longopts)
        for key, value in optlist:
            if key == '-c':
                config_path = value

        if config_path:
            logging.info('loading config from %s' % config_path)
            with open(config_path, 'rb') as f:
                try:
                    config = parse_json_in_str(f.read().decode('utf8'))
                except ValueError as e:
                    logging.error('found an error in config.json: %s',
                                  e.message)
                    sys.exit(1)
    
```

代码具体什么逻辑这里不再说明,重要的是,从这段代码中提取两个跟本博客相关的名字`sys.argv`和`getopt`,看看大牛是怎么用的,然后总结自己的使用方法,这就是提高吧,下一步是分析各种方法并比较出优缺点.


## 常见命令行解析模块的使用方法

这里通过一个端口扫描脚本来验证各个模块的使用方法.

### sys.argv

### optget

### 
