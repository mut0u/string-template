# string-template

这个库是用来生成生成Clojure的模板字符串的库。


灵感来源于同事。Clojure 的主要的ORM 是 honeySQL， 这个设计是非常好的，但是偶尔也觉得对于复杂的SQL, 需要使用raw的方式写SQL。所以写了一个简单的字符串转换的库， 会生成jdbc需要的参数来执行SQL


其中有两个特殊符号`#{}`， `#[]`

示例如下：

```
  (let [id 1 v ["aa" "bb"]]
    (sql-readers @"select * from user where id = #{id} "
                 (if true
                   @"id = 5"
                   @"id = 6"
                   )
                 @"and id in #[v] and active = true"))


```

返回结果：
```
["select * from users where id = ?  id = 5 and i in (?,?) and active = true" 1 "aa" "bb"]
```




## Usage

FIXME
