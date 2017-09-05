package org.throwable.shiro.support.cache;

import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.support.serialize.DefaultShiroSerializer;
import org.throwable.shiro.support.serialize.FastJsonSerializer;
import org.throwable.shiro.support.serialize.Serializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collection;
import java.util.Set;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 19:00
 */
public abstract class AbstractRedisTemplate<K, V> {

	private static final String KEY_PREFIX = "shiro_redis_cache:";

	private Serializer serializer;
	private JedisPoolConfig jedisPoolConfig;

	public AbstractRedisTemplate(ShiroProperties shiroProperties) {
		this.serializer = new DefaultShiroSerializer();
		this.jedisPoolConfig = createJedisPool(shiroProperties);
	}

	protected JedisPoolConfig createJedisPool(ShiroProperties shiroProperties) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(shiroProperties.getRedisPoolMaxIdle());
		jedisPoolConfig.setMinIdle(shiroProperties.getRedisPoolMinIdle());
		jedisPoolConfig.setMaxTotal(shiroProperties.getRedisPoolMaxActive());
		jedisPoolConfig.setMaxWaitMillis(shiroProperties.getRedisPoolMaxWait());
		return jedisPoolConfig;
	}

	protected JedisPoolConfig getJedisPoolConfig() {
		return jedisPoolConfig;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	protected Serializer getSerializer() {
		return serializer;
	}

	public abstract V getValueByKey(K k);

	public abstract V putValueByKey(K k, V v);

	public abstract V removeValueByKey(K k);

	public abstract void clear();

	public abstract Long fetchDatabaseSize();

	public abstract Set<K> queryKeysByPattern();

	public abstract Set<K> queryKeysByPattern(String keyPattern);

	public abstract Collection<V> queryAllValues();

	public abstract Collection<V> queryAllValues(String keyPattern);

	public abstract V putValueByKey(K k, V v, int expireSeconds);

	public abstract Long increment(K k, long time);

	protected byte[] getKeyBytes(K k) {
		if (k instanceof String) {
			return (KEY_PREFIX + k).getBytes();
		} else {
			return getSerializer().serialize(k);
		}
	}

	protected String getDefaultKeyPattern() {
		return KEY_PREFIX + "*";
	}
}
