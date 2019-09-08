简介
--
>dodo取名源于已灭绝鸟类，渡渡鸟/多多鸟（Dodo bird）  
dodo-rpc致力于成为代码清晰简洁、高性能的rpc框架  


已实现及计划的模块
--
模块 | 已实现 | 计划
--- | --- | ---
通讯 | netty | N
序列化 | protostuff/fst/fastjson/dsljson | N
反射 | javassist/jdk | N
负载均衡 | random/roundrobin(weight)/consistent-hashing/weighted-response-time | N
集群HA | failfast/failover/failback/failsafe | N
过滤器 | rate-limit/token-rate-limit/statistics/access-log/mock | trace(CAT)
框架整合 | spring/spring-boot | spring-boot-starter
配置中心 | N | nacos
注册中心 | zookeeper/local4test | nacos
协议 | dodo | grpc/restful/http2
治理中心 | N | api/ui
