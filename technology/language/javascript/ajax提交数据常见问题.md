1. 提交含有数组的数据

```
//要上传的数据
data = {"rd": ["123", "1234", "12345"]}
//一般的上传方式
$.ajax({
    url: "/v1/service/update",
    type: "PUT",
    async: true,
    data: data,
    dataType: "json",
})
//服务端得到的数据为
{'rd[]': '123'}

//正确的上传方式为使用JSON对象的stringify方法
data = {"rd": JSON.stringify(["123", "1234", "12345"])}
$.ajax({
    url: "/v1/service/update",
    type: "PUT",
    async: true,
    data: data,
    dataType: "json",
})

```
