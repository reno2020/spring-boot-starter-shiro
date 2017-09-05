package org.throwable.shiro.support.cache;

import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.utils.Asserts;
import org.throwable.shiro.utils.Strings;
import redis.clients.jedis.JedisPool;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 19:04
 */
public class SingleRedisTemplate<K, V> extends AbstractJedisDelegateTemplate<K, V> {

    private final String host;
    private final int port;
    private String password;
    private final ShiroProperties shiroProperties;

    public SingleRedisTemplate(String host, int port, String password, ShiroProperties shiroProperties) {
        super(shiroProperties);
        this.host = host;
        this.port = port;
        this.password = password;
        this.shiroProperties = shiroProperties;
        init();
    }

    private void init() {
        Asserts.notEmpty(host, "Single redis host must not be empty!");
        if (Strings.isEmpty(password)) {
            this.password = null;
        }
        JedisPool jedisPool = new JedisPool(getJedisPoolConfig(), host, port, shiroProperties.getRedisTimeout(), password,
                shiroProperties.getRedisDatabase());
        super.setJedisPool(jedisPool);
    }

}
