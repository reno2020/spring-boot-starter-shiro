package org.throwable.shiro.support.cache;

import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.DisposableBean;
import org.throwable.shiro.configuration.ShiroProperties;
import redis.clients.jedis.BinaryJedisCluster;
import redis.clients.jedis.JedisCluster;

import java.util.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 0:59
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJedisClusterDelegateTemplate<K, V> extends AbstractRedisTemplate<K, V> implements DisposableBean {

	private JedisCluster delegate;

	public AbstractJedisClusterDelegateTemplate(ShiroProperties shiroProperties) {
		super(shiroProperties);
	}

	protected V execute(RedisClusterCallback<V> callback) {
		return callback.doInRedisCluster(getJedisCluster());
	}

	protected void executeVoid(RedisClusterCallback<Void> callback) {
		callback.doInRedisCluster(getJedisCluster());
	}

	protected <T> T executeCustom(RedisClusterCallback<T> callback) {
		return callback.doInRedisCluster(getJedisCluster());
	}

	@Override
	public V getValueByKey(final K k) {
		return execute(jedisCluster -> (V) getSerializer().deserialize(jedisCluster.get(getKeyBytes(k))));
	}

	@Override
	public V putValueByKey(final K k, final V v) {
		return execute(jedisCluster -> {
			V preValue = getValueByKey(k);
			jedisCluster.set(getKeyBytes(k), getSerializer().serialize(v));
			return preValue;
		});
	}

	@Override
	public V removeValueByKey(final K k) {
		return execute(jedisCluster -> {
			V preValue = getValueByKey(k);
			jedisCluster.del(getKeyBytes(k));
			return preValue;
		});
	}

	@Deprecated
	@Override
	public void clear() {
		executeVoid(jedisCluster -> {
			jedisCluster.flushDB();
			return null;
		});
	}

	@Deprecated
	@Override
	public Long fetchDatabaseSize() {
		return executeCustom(BinaryJedisCluster::dbSize);
	}

	@Override
	public Set<K> queryKeysByPattern() {
		return queryKeysByPattern(getDefaultKeyPattern());
	}

	@Override
	public Set<K> queryKeysByPattern(final String keyPattern) {
		return executeCustom(jedisCluster -> {
			Set<byte[]> keys = jedisCluster.hkeys(keyPattern.getBytes());
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
		return executeCustom(jedisCluster -> {
			Collection<byte[]> values = jedisCluster.hvals(keyPattern.getBytes());
			if (CollectionUtils.isEmpty(values)) {
				return Collections.emptyList();
			}
			List<V> results = new ArrayList<>();
			for (byte[] value : values) {
				results.add((V) getSerializer().deserialize(value));
			}
			return results;
		});
	}

	@Override
	public V putValueByKey(final K k, final V v, final int expireSeconds) {
		return execute(jedisCluster -> {
			V preValue = getValueByKey(k);
			jedisCluster.setex(getKeyBytes(k), expireSeconds, getSerializer().serialize(v));
			return preValue;
		});
	}

	@Override
	public Long increment(K k, final long time) {
		return executeCustom(jedisCluster -> jedisCluster.incrBy(getKeyBytes(k), time));
	}

	public JedisCluster getJedisCluster() {
		return delegate;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.delegate = jedisCluster;
	}

	@Override
	public void destroy() throws Exception {
		if (null != delegate) {
			delegate.close();
		}
	}
}
