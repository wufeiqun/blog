## 常用elasticsearch命令总结

#### 删除索引

* 删除单个索引

```
DELETE /newsearch-2023.06.30

​curl -X DELETE http://192.168.1.1:9200/​​newsearch-2023.06.30

```

* 通配符删除索引


```
DELETE /newsearch-*

​curl -X DELETE http://192.168.1.1:9200/​​newsearch-*
```

默认ES是关闭通配符操作的, 需要改一个配置:

```
By default, this parameter does not support wildcards (*) or _all. To use wildcards or _all, set the action.destructive_requires_name cluster setting to false.
```