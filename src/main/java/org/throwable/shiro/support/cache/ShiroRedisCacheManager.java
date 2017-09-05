package org.throwable.shiro.support.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 18:42
 */
public class ShiroRedisCacheManager implements CacheManager {

	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();
	private final AbstractRedisTemplate redisTemplate;

	public ShiroRedisCacheManager(AbstractRedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		Cache cache = this.caches.get(name);
		if (null == cache) {
			cache = new ShiroRedisCache(redisTemplate);
			this.caches.put(name, cache);
		}
		return cache;
	}
}
