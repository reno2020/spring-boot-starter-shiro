package org.throwable.shiro.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/5 17:30
 */
@Slf4j
public class ShiroLoggingSessionListener implements SessionListener {

    @Override
    public void onStart(Session session) {
        if (log.isDebugEnabled()) {
            log.debug("ShiroLoggingSessionListener listens onStart,session id:{}", session.getId());
        }
    }

    @Override
    public void onStop(Session session) {
        if (log.isDebugEnabled()) {
            log.debug("ShiroLoggingSessionListener listens onStop,session id:{}", session.getId());
        }
    }

    @Override
    public void onExpiration(Session session) {
        if (log.isDebugEnabled()) {
            log.debug("ShiroLoggingSessionListener listens onExpiration,session id:{}", session.getId());
        }
    }
}
