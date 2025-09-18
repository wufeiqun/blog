#### 简单问题记录

* 使用openjdk8-alpine镜像, 镜像中的telnet, 低版本不好用, 检测端口没有任何输出, 升级alphine Linux版本或者从高版本中拷贝一个telnet文件就行

* 强制删除某一个node节点上的POD

```
kubectl get pod -A -o wide | grep k8s-worker-01 | grep Terminating| awk '{print "kubectl delete pod "$2" -n "$1" --force --grace-period=0"}' | sh
```
