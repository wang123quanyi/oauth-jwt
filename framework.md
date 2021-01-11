## 此结构是在网关层面根据token 认证、鉴权

````
网关登录:http://localhost:8888/auth/oauth/token?grant_type=password&username=macro&password=123456&scope=all
        Basic Auth : Username:order-client Password:order-secret-8888
````

#### 需要鉴权的接口需要在请求头添加如下
````
Authorization : bearer token
````

#### nacos公共配置

``````
在bootstrap.yml 里nacos:config: 下配置 
shared-configs[0]: 
  data-id: redis.yml(nacos配置列表里的配置)
  refresh: true(支持刷新)
  
例(1)redis.yml：
spring:
  main:
    allow-bean-definition-overriding: true
  redis:
    database: 2
    host: 192.168.1.181
    jedis:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: 30000ms
        min-idle: 1
    password: campo
    port: 6379
    timeout: 6000ms

例(2)feign-mybatis.yml:
spring:
  main:
    allow-bean-definition-overriding: true
#
feign:
  hystrix:
    enabled: true
  okhttp:
    enabled: true
  httpclient:
    enabled: false
    connection-timeout: 20000
    connection-timer-repeat: 20000
    max-connections: 200
    max-connections-per-route: 50
  client:
    config:
      default:
        connectTimeout: 10000
        readTimeout: 10000
  compression:
    request:
      enabled: true
    response:
      enabled: true

## hystrix 配置
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 5000

###请求处理的超时时间
ribbon:
  ReadTimeout: 6000
  ConnectTimeout: 6000
  
mybatis-plus:
  mapper-locations: classpath:/mapper/**/*Mapper.xml
``````

#### 在使用@EnableFeignClients时需要加载 @FeignClient 所在包，同时需要@ComponentScan 加载熔断相关类

#### 账号登出、新建、网关鉴权逻辑
``````
登出和新建都需在授权模块处理
登出时将token jti 放入redis作为黑名单，在网关层进行拦截
``````

#### 关于token时效
````
目前放在jwt增强里定义了时效，在网关层进行判断处理
````

#### 关于服务间互调
````
服务间互调请求头中的用户信息丢失，所以在服务间互调时需要将用户信息作为参数传递
````