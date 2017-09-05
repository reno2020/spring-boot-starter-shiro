package org.throwable.shiro.support.cache;

import redis.clients.jedis.Jedis;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 23:47
 */
public interface RedisCallback<T> {

	T doInRedis(Jedis jedis);
}
