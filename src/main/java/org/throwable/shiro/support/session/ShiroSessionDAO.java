package org.throwable.shiro.support.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.throwable.shiro.support.cache.AbstractRedisTemplate;
import org.throwable.shiro.utils.Asserts;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 1:35
 */
public class ShiroSessionDAO extends AbstractSessionDAO {

	private static final String SESSION_KEY_PREFIX = "shiro_redis_session:";
	private final int sessionTimeoutSeconds;
	private final AbstractRedisTemplate<String, Session> redisTemplate;

	public ShiroSessionDAO(AbstractRedisTemplate<String, Session> redisTemplate, int sessionTimeoutSeconds) {
		this.redisTemplate = redisTemplate;
		this.sessionTimeoutSeconds = sessionTimeoutSeconds;
	}

	@Override
	protected Serializable doCreate(Session session) {
		Asserts.notNull(session, "Session to create must not be null!");
		Serializable sessionId = this.generateSessionId(session);
		this.assignSessionId(session, sessionId);
		redisTemplate.putValueByKey(buildRedisSessionKey(sessionId), session, sessionTimeoutSeconds);
		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		Asserts.notNull(sessionId, "SessionId of session to read must not be null!");
		return redisTemplate.getValueByKey(buildRedisSessionKey(sessionId));
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		Asserts.notNull(session, "Session to update must not be null!");
		redisTemplate.putValueByKey(buildRedisSessionKey(session.getId()), session, sessionTimeoutSeconds);
	}

	@Override
	public void delete(Session session) {
		Asserts.notNull(session, "Session to delete must not be null!");
		redisTemplate.removeValueByKey(buildRedisSessionKey(session.getId()));
	}

	@Override
	public Collection<Session> getActiveSessions() {
		return redisTemplate.queryAllValues(buildRedisSessionKeyPattern());
	}

	private String buildRedisSessionKey(Serializable sessionId) {
		return SESSION_KEY_PREFIX + sessionId;
	}

	private String buildRedisSessionKeyPattern() {
		return SESSION_KEY_PREFIX + "*";
	}
}
