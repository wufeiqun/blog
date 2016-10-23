> 工欲善其事，必先利其器

## 前言

&emsp;自从买了Macpro后,开发效率大大提升,很大一部分的原因是Mac平台上有众多的优秀工具,接下来一段时间我会总结一下工作中用到的工具的配置以及快捷键,欢迎大家一起交流学习,这一篇是强大的oh-my-zsh.

## 为什么使用oh-my-zsh

&emsp;接触Linux已经有3年啦,绝大多数的Linux终端默认的shell是bash,所以就认为bash是最好用的shell啦,毕竟是官方默认的嘛,工作以后看到很多同事的终端五彩斑斓,各种羡慕啊~后来才知道他们使用的是zsh,经过一段时间的研究使用,发现太强大啦:

1. 兼容绝大部分的bash命令(至今使用过的命令都是兼容的)
2. 强大的命令补全功能,这货居然可以补全参数,太强了
3. 插件功能,网上有丰富的第三方插件,比如git,autojump等
4. 丰富的配置选项,颜色主题等
5. balabala...

## 常见配置选项

&emsp;这里就不说怎么安装啦,按照官方文档走一遍就行了.

```


# Path to your oh-my-zsh installation.
# ZSH的环境变量
export ZSH=/Users/rocky/.oh-my-zsh

# Set name of the theme to load.
# Look in ~/.oh-my-zsh/themes/
# Optionally, if you set this to "random", it'll load a random theme each
# time that oh-my-zsh is loaded.
# 主题设定
ZSH_THEME="agnoster"

# Uncomment the following line to use case-sensitive completion.
# 大小写敏感,这里设定为不区分大小写
#CASE_SENSITIVE="true"

# Uncomment the following line to use hyphen-insensitive completion. Case
# sensitive completion must be off. _ and - will be interchangeable.
# 连接符不敏感设定,不敏感
HYPHEN_INSENSITIVE="true"

# Uncomment the following line to disable bi-weekly auto-update checks.
# 自动更新
# DISABLE_AUTO_UPDATE="true"

# Uncomment the following line to change how often to auto-update (in days).
# 自动更新时间间隔
export UPDATE_ZSH_DAYS=30

# Uncomment the following line to disable colors in ls.
# ls彩色是否禁止,当然不禁止啦
# DISABLE_LS_COLORS="true"

# Uncomment the following line to disable auto-setting terminal title.
# 是否禁止更改终端标题,不要禁止,不然所有终端tab只显示zsh了,而不随着目录的改变而改变显示
# DISABLE_AUTO_TITLE="true"

# Uncomment the following line to enable command auto-correction.
# 自动纠正命令,不启用,不怎么好用,有点乱纠正
# ENABLE_CORRECTION="true"

# Uncomment the following line to display red dots whilst waiting for completion.
# 按tab键补全命令的时候,如果没什么可补全的就会出现三个红点,更人性化显示
COMPLETION_WAITING_DOTS="true"

# Uncomment the following line if you want to disable marking untracked files
# under VCS as dirty. This makes repository status check for large repositories
# much, much faster.
# 这个不知道啥意思,有知道的朋友可以告诉我呀,谢谢啦
# DISABLE_UNTRACKED_FILES_DIRTY="true"

# Uncomment the following line if you want to change the command execution time
# stamp shown in the history command output.
# The optional three formats: "mm/dd/yyyy"|"dd.mm.yyyy"|"yyyy-mm-dd"
# 历史命令日期显示格式
HIST_STAMPS="mm/dd/yyyy"

# Would you like to use another custom folder than $ZSH/custom?
# ZSH_CUSTOM=/path/to/new-custom-folder

# 自带插件位置
# Which plugins would you like to load? (plugins can be found in ~/.oh-my-zsh/plugins/*)
# 自定义插件存放位置
# Custom plugins may be added to ~/.oh-my-zsh/custom/plugins/
# Example format: plugins=(rails git textmate ruby lighthouse)
# Add wisely, as too many plugins slow down shell startup.
# 定义要加载的插件
plugins=(git autojump)

[[ -s ~/.autojump/etc/profile.d/autojump.zsh ]] && . ~/.autojump/etc/profile.d/autojump.zsh

# User configuration

# 环境变量
export PATH="/Library/Frameworks/Python.framework/Versions/2.7/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"
# export MANPATH="/usr/local/man:$MANPATH"

source $ZSH/oh-my-zsh.sh

# You may need to manually set your language environment
export LANG=en_US.UTF-8

# Preferred editor for local and remote sessions
# if [[ -n $SSH_CONNECTION ]]; then
#   export EDITOR='vim'
# else
#   export EDITOR='mvim'
# fi

# Compilation flags
# export ARCHFLAGS="-arch x86_64"

# ssh
# export SSH_KEY_PATH="~/.ssh/dsa_id"

# Set personal aliases, overriding those provided by oh-my-zsh libs,
# plugins, and themes. Aliases can be placed here, though oh-my-zsh
# users are encouraged to define aliases within the ZSH_CUSTOM folder.
# For a full list of active aliases, run `alias`.
#
# Example aliases
# alias zshconfig="mate ~/.zshrc"
# alias ohmyzsh="mate ~/.oh-my-zsh"
source ~/pyenv/bin/activate
alias vim='mvim -v'
export EDITOR='vim'
```

## 插件以及使用方式

*  git

```
**g** 		git
**gl** 		git pull
**gp** 		git push
**ga** 		git add
**gcm** 		git checkout master
**gst** 		git status
**gba** 		git branch -a #列出所有的分支,包括远程和本地的
**glgg** 		git log --graph --max-count=5 #看日志
**grh** 		git reset HEAD #恢复到add文件之间的当前分支版本,一般是把错误的修改add到了缓存区而没有提交之前.
**grhh** 		git reset HEAD --hard 
**gpa** 		git add .; git commit -m "$1"; git push; # only in the ocodo fork.
...
还有好多没有列出来,这里只是常用的,其他的请参考下面链接部分.
```

*  brew

输入`brew in`,按tab的话会自动补全`brew install`,其他功能类似.

*  autojump

假如你已经进去过`/home/test/haha/meme/gugu`,你就可以从任意目录输入`j + gugu`(路径中的某些字母,再敲TAB键,就可以进去那个目录了) 

*  递归查找

`ls **/*.log`


## 参考链接

[使用ZSH的九个理由](http://blog.jobbole.com/28829/)
[Why Zsh](https://www-s.acm.illinois.edu/workshops/zsh/why.html)
[oh-my-zsh wiki:git](https://github.com/robbyrussell/oh-my-zsh/wiki/Plugin:git)

