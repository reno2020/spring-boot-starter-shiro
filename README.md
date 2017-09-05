# spring-boot-shiro-starter

### 简介

基于SpringBoot和Shiro编写的插件，简化Shiro的操作。

### 依赖

* spring-boot-starter-web，版本1.5.6.RELEASE。
* spring-boot-starter-aop，版本1.5.6.RELEASE。
* shiro-spring，版本1.4.0。
* jedis，版本2.9.0。
* fastjson，版本1.2.37。

### 安装

*  git clone https://github.com/zjcscut/spring-boot-starter-shiro.git
*  cd spring-boot-starter-shiro
*  mvn clean install


maven依赖：

``` xml
        <dependency>
            <groupId>org.throwable</groupId>
            <artifactId>spring-boot-shiro-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```

### 使用教程

#### 配置详解

spring-boot-shiro-starter希望把**尽量多的操作进行配置化**，应用于SpringBoot的配置文件如\*.properties文件或者\*.yaml文件，下面是详细的配置列表：

* **spring.shiro.redis-nodes：**Redis配置，由于Shiro使用在服务集群需要Session共享，使用了Redis实现，支持Redis Single(单节点)模式、Sentinel(哨兵)模式、Cluster(集群)模式，三种模式的本字段配置如下：
  * Redis[host:port]
  * Sentinel\[masterName\]\[host1:port1,host2:port2...\]
  * Cluster\[host1:port1,host2:port2...\]
* **spring.shiro.redis-password：**Redis密码，默认值为null。
* **spring.shiro.redis-timeout：**Redis连接超时时间，单位毫秒。
* **spring.shiro.redis-max-attempts：**Redis-Cluster模式下的最大尝试次数。
* **spring.shiro.redis-pool-max-idle：**Redis连接池配置，对应maxIdle，默认值为8.
* **spring.shiro.redis-pool-min-idle：**Redis连接池配置，对应minIdle，默认值为0。
* **spring.shiro.redis-pool-max-active：**Redis连接池配置，对应maxTotal，默认值为8。
* **spring.shiro.redis-pool-max-wait：**Redis连接池配置，对应maxWaitMillis，单位毫秒，默认值为-1。
* **spring.shiro.redis-database：**Redis数据库编号，默认值为0.
* **spring.shiro.session-timeout-seconds：**Session超时时间，单位秒，默认值为1800。
* **spring.shiro.enable-unauthorized-redirect：**非授权状态下是否允许重定向，默认为false，如果此时抛出了非授权类型异常，会直接返回JSON，code为403，message为对应异常的e.getMessage( )。
* **spring.shiro.unauthorized-redirect-url：**非授权状态下，如果允许重定向，重定向的url，默认值为/unauthorized。
* **spring.shiro.login-url：**登录页面的url，注意，不是登陆表单提交的url，默认值为/login。
* **spring.shiro.logout-url：**退出登录的url，默认值为/logout。
* **spring.shiro.login-success-redirect-url：**登录成功后重定向的目标url，默认值为/。
* **spring.shiro.logout-success-redirect-url：**退出登录成功后重定向的目标url，默认值为/login。
* **spring.shiro.enable-login-lock：**是否允许登录锁定，默认值为false。
* **spring.shiro.max-login-fail-attempts：**当允许登录锁定时，最大的登录失败次数，默认值为5。
* **spring.shiro.login-lock-seconds：**当允许登录锁定并且超过最大登录失败次数，账号被锁定的时间，单位秒，默认值为86400(24小时)。
* **spring.shiro.locale：**国际化配置locale，格式为language_conntry，默认值为zh_CN。
* **spring.shiro.ini-file-location：**\*.ini文件的位置，只对[urls]配置生效，默认值为null。
* **spring.shiro.enable-shiro-realm：**是否允许spring-boot-shiro-starter内置的SimpleShiroRealm注册为Spring容器中的Bean，默认值为false。
* **spring.shiro.password-param：**登录表单中密码字段的name(key)，默认值为password。
* **spring.shiro.username-param：**登录表单中用户名字段的name(key)，默认值为username。
* **spring.shiro.remember-me-param：**登录表单中记住我字段的name(key)，默认值为rememberMe。
* **spring.shiro.max-cookie-age：**rememberMe下Cookie的过期时间，单位秒，默认值为2592000(30)天。
* **spring.shiro.cipher-key：**Session加密密钥，默认值为null，null的时候使用默认的生成规则，规则见下文。
* **spring.shiro.delete-invalid-sessions：**是否允许自动清理无效的Session，默认值为true。
* **spring.shiro.session-cookie-name：**携带Session的Cookie的名称，默认值为SHIRO-SESSIONID，避免和Jetty、Tomcat等Servlet容器的JSESSIONID冲突。
* **spring.shiro.remember-me-cookie-name：**rememberMe下Cookie的名称，默认值为SHIRO-REMEMBERME。
* **spring.shiro.enable-anonymous：**如果当前的拦截规则链不存在以'/\*\*'为key的规则，则在拦截规则链的尾添加一个规则，key为'/\*\*'，value为'anon'，默认值为true。

#### 

#### cipher-key的生成规则

如果cipher-key配置为null，spring-boot-shiro-starter内部将生成一个值，生成规则如下：

```java
KeyGenerator keygen = KeyGenerator.getInstance("AES");
SecretKey secretKey = keygen.generateKey();
String cipherKey = Base64.encodeToString(secretKey.getEncoded());
```



#### 注意事项

* 配置属性项
* Spring容器中至少存在一个org.apache.shiro.realm.Realm接口实现的Bean。

#### 登录操作



#### 退出登录操作



#### 拦截器以及拦截规则配置



#### Session监听



#### Demo

**Demo项目见下面的Links部分**，请确保本地安装了Redis才能正常启动。

### Contact

**author：**throwable

**email：**[739805340@qq.com](mailto:739805340@qq.com)

Help yourselves!

### Links

* [doge-mapper，一款轻巧而强大Mybatis插件](https://github.com/zjcscut/doge-mapper)
* [demo项目]()

### PS

**以后将会有一个支持单点登录的版本，希望支持。**

