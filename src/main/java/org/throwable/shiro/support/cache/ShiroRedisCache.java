package org.throwable.shiro.support.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 13:06
 */
@Slf4j
public class ShiroRedisCache<K, V> implements Cache<K, V> {

	private final AbstractRedisTemplate<K, V> delegate;

	public ShiroRedisCache(AbstractRedisTemplate<K, V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public V get(K k) throws CacheException {
		return delegate.getValueByKey(k);
	}

	@Override
	public V put(K k, V v) throws CacheException {
		return delegate.putValueByKey(k, v);
	}

	@Override
	public V remove(K k) throws CacheException {
		return delegate.removeValueByKey(k);
	}

	@Override
	public void clear() throws CacheException {
		delegate.clear();
	}

	@Override
	public int size() {
		return delegate.fetchDatabaseSize().intValue();
	}

	@Override
	public Set<K> keys() {
		return delegate.queryKeysByPattern();
	}

	@Override
	public Collection<V> values() {
		return delegate.queryAllValues();
	}
}
