package org.throwable.shiro.support.cache;

import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.utils.Asserts;
import org.throwable.shiro.utils.Strings;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Set;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 20:01
 */
@SuppressWarnings("unchecked")
public class SentinelRedisTemplate<K, V> extends AbstractJedisDelegateTemplate<K, V> {

	private final Set<String> sentinels;
	private final String masterName;
	private final String password;
	private final ShiroProperties shiroProperties;

	public SentinelRedisTemplate(Set<String> sentinels,
								 String masterName,
								 ShiroProperties shiroProperties) {
		super(shiroProperties);
		this.sentinels = sentinels;
		this.password = shiroProperties.getRedisPassword();
		this.masterName = masterName;
		this.shiroProperties = shiroProperties;
		init();
	}


	private void init(){
		Asserts.notEmpty(masterName, "MasterName of redis sentinels must not be empty!");
		Asserts.notEmpty(sentinels, "Sentinel nodes of redis sentinels must not be empty!");
		JedisSentinelPool jedisSentinelPool;
		if (Strings.isNotEmpty(password)) {
			jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, getJedisPoolConfig(), shiroProperties.getRedisTimeout(),
					password, shiroProperties.getRedisDatabase());
		} else {
			jedisSentinelPool = new JedisSentinelPool(masterName, sentinels, getJedisPoolConfig(), shiroProperties.getRedisTimeout(),
					null, shiroProperties.getRedisDatabase());
		}
		super.setJedisPool(jedisSentinelPool);
	}
}
