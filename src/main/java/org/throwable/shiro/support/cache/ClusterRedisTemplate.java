package org.throwable.shiro.support.cache;

import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.utils.Asserts;
import org.throwable.shiro.utils.Strings;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.Set;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 20:01
 */
public class ClusterRedisTemplate<K, V> extends AbstractJedisClusterDelegateTemplate<K, V> {

	private final Set<HostAndPort> nodes;
	private String password;
	private final ShiroProperties shiroProperties;

	public ClusterRedisTemplate(Set<HostAndPort> nodes, ShiroProperties shiroProperties) {
		super(shiroProperties);
		this.nodes = nodes;
		this.password = shiroProperties.getRedisPassword();
		this.shiroProperties = shiroProperties;
		init();
	}

	private void init(){
		Asserts.notEmpty(nodes, "Redis cluster nodes must not be empty!");
		if (Strings.isEmpty(password)) {
			this.password = null;
		}
		JedisCluster jedisCluster = new JedisCluster(nodes, shiroProperties.getRedisTimeout(), shiroProperties.getRedisTimeout(),
				shiroProperties.getRedisMaxAttempts(), password, getJedisPoolConfig());
		super.setJedisCluster(jedisCluster);
	}
}
