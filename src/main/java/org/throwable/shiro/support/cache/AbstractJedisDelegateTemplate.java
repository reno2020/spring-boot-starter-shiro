package org.throwable.shiro.support.cache;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.throwable.shiro.configuration.ShiroProperties;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 0:48
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJedisDelegateTemplate<K, V> extends AbstractRedisTemplate<K, V> implements DisposableBean {

	private Pool<Jedis> delegate;

	public AbstractJedisDelegateTemplate(ShiroProperties shiroProperties) {
		super(shiroProperties);
	}

	protected V execute(RedisCallback<V> callback) {
		try (Jedis jedis = delegate.getResource()) {
			return callback.doInRedis(jedis);
		}
	}

	protected void executeVoid(RedisCallback<Void> callback) {
		try (Jedis jedis = delegate.getResource()) {
			callback.doInRedis(jedis);
		}
	}

	protected <T> T executeCustom(RedisCallback<T> callback) {
		try (Jedis jedis = delegate.getResource()) {
			return callback.doInRedis(jedis);
		}
	}

	@Override
	public V getValueByKey(final K k) {
		return execute(jedis -> (V) getSerializer().deserialize(jedis.get(getKeyBytes(k))));
	}

	@Override
	public V putValueByKey(final K k, final V v) {
		return execute(jedis -> {
			V preValue = getValueByKey(k);
			jedis.set(getKeyBytes(k), getSerializer().serialize(v));
			return preValue;
		});
	}

	@Override
	public V removeValueByKey(final K k) {
		return execute(jedis -> {
			V preValue = getValueByKey(k);
			jedis.del(getKeyBytes(k));
			return preValue;
		});
	}

	@Override
	public void clear() {
		executeVoid(jedis -> {
			jedis.flushDB();
			return null;
		});
	}

	@Override
	public Long fetchDatabaseSize() {
		return executeCustom(BinaryJedis::dbSize);
	}

	@Override
	public Set<K> queryKeysByPattern() {
		return queryKeysByPattern(getDefaultKeyPattern());
	}

	@Override
	public Set<K> queryKeysByPattern(final String keyPattern) {
		return executeCustom(jedis -> {
			Set<byte[]> keys = jedis.keys(keyPattern.getBytes());
			if (CollectionUtils.isEmpty(keys)) {
				return Collections.emptySet();
			}
			Set<K> results = new HashSet<>();
			for (byte[] key : keys) {
				results.add((K) getSerializer().deserialize(key));
			}
			return results;
		});
	}

	@Override
	public Collection<V> queryAllValues() {
		return queryAllValues(getDefaultKeyPattern());
	}

	@Override
	public Collection<V> queryAllValues(final String keyPattern) {
		return executeCustom(jedis -> {
			Set<byte[]> keys = jedis.keys(keyPattern.getBytes());
			if (CollectionUtils.isEmpty(keys)) {
				return Collections.emptyList();
			}
			List<V> results = new ArrayList<>();
			for (byte[] key : keys) {
				results.add((V) getSerializer().deserialize(jedis.get(key)));
			}
			return results;
		});
	}

	@Override
	public V putValueByKey(final K k, final V v, final int expireSeconds) {
		return execute(jedis -> {
			V preValue = getValueByKey(k);
			jedis.setex(getKeyBytes(k), expireSeconds, getSerializer().serialize(v));

			return preValue;
		});
	}

	@Override
	public Long increment(K k, final long time) {
		return executeCustom(jedis -> jedis.incrBy(getKeyBytes(k), time));
	}

	public Pool<Jedis> getJediPool() {
		return delegate;
	}

	public void setJedisPool(Pool<Jedis> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void destroy() throws Exception {
		if (null != delegate && !delegate.isClosed()) {
			this.delegate.destroy();
		}

	}
}
