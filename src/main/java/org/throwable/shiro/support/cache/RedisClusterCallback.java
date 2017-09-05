package org.throwable.shiro.support.cache;

import redis.clients.jedis.JedisCluster;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 1:16
 */
public interface RedisClusterCallback<T> {

	T doInRedisCluster(JedisCluster jedisCluster);
}
