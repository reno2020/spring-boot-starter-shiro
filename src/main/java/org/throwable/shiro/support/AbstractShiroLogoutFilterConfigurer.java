package org.throwable.shiro.support;

import org.apache.shiro.subject.Subject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/5 11:06
 */
public abstract class AbstractShiroLogoutFilterConfigurer {

    protected abstract void processBeforeLogout(Subject subject, ServletRequest request, ServletResponse response);

    protected abstract void processAfterLogout(Subject subject, ServletRequest request, ServletResponse response);

    protected abstract void processBeforeLogoutRedirect(Subject subject, ServletRequest request, ServletResponse response, String redirectUrl);

}
