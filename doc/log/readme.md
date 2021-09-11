#  log4j  log4j2  slf4j  

> logback 实现了 slf4j api, 不需要桥接器
> 
>

![](https://gitee.com/niubenwsl/image_repo/raw/master/image/java/20210428000922.png)


'ch.qos.logback:logback-core:1.2.3'

'ch.qos.logback:logback-classic:1.2.3'



'org.slf4j:jcl-over-slf4j:1.7.30'     将 jakarta commons logging 日志框架 到 slf4j 的桥接

'org.slf4j:jul-to-slf4j:1.7.30'       将 jva.util.logging 日志框架 到 slf4j 的桥接

'org.slf4j:log4j-over-slf4j:1.7.30'   将 log4j 日志框架 到 slf4j 的桥接

'org.slf4j:osgi-over-slf4j:1.7.30'    将 osgi 环境下的日志 到 slf4j 的桥接



- 'org.slf4j:slf4j-api:1.7.30'       slf4j的api接口

'org.slf4j:slf4j-jcl:1.7.30'       slf4j 转接到 jakarta commons logging 日志输出框架 (这个包不能和 jcl-over-slf4j 混用,否则会 死循环)

'org.slf4j:slf4j-jdk14:1.7.30'      slf4j 转接到 java.util.logging 日志输出框架 (这个包不能和 jul-to-slf4j 混用,否则会 死循环)

'org.slf4j:slf4j-log4j12:1.7.30'    slf4j 转接到 log4j, (这个包不能和 log4j-over-slf4j混用,否则会 死循环)

'org.slf4j:slf4j-nop:1.7.30'        slf4j 空接口输出绑定(丢弃所有日志输出)

'org.slf4j:slf4j-simple:1.7.30'      slf4j 自带的简单日志输出接口



- 如果不同的日志框架 混用:

'org.apache.logging.log4j:log4j-1.2-api:2.14.1'    将 log4j 日志转接到 log4j2

'org.apache.logging.log4j:log4j-api:2.14.1'        log4j2 日志api

'org.apache.logging.log4j:log4j-core:2.14.1'       log4j2 日志输出核心

'org.apache.logging.log4j:log4j-slf4j-impl:2.14.1' slf4j 日志转接到 log4j2 (不能与 log4j-to-slf4j 混用)

'org.apache.logging.log4j:log4j-to-slf4j:2.14.1'   log4j2 日志转接到 slf4j (不能与 log4j-slf4j-impl 混用 )





> 一般项目中使用  log4j2 来实现日志打印, 如果存在多种日志框架混用的情况: 
> log4j -> log4j2
> slf4j -> log4j2






