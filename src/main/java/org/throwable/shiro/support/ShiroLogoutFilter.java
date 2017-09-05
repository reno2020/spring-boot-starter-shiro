package org.throwable.shiro.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.util.WebUtils;
import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.utils.Strings;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/5 11:07
 */
@Slf4j
public class ShiroLogoutFilter extends LogoutFilter {

    private ShiroProperties shiroProperties;
    private Collection<AbstractShiroLogoutFilterConfigurer> abstractShiroLogoutFilterConfigurers;

    public ShiroLogoutFilter(ShiroProperties shiroProperties,
                             Collection<AbstractShiroLogoutFilterConfigurer> abstractShiroLogoutFilterConfigurers) {
        this.shiroProperties = shiroProperties;
        if (CollectionUtils.isEmpty(abstractShiroLogoutFilterConfigurers)) {
            abstractShiroLogoutFilterConfigurers = new LinkedList<>();
        }
        this.abstractShiroLogoutFilterConfigurers = abstractShiroLogoutFilterConfigurers;
    }

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        // Check if POST only logout is enabled
        if (isPostOnlyLogout()) {
            // check if the current request's method is a POST, if not redirect
            if (!WebUtils.toHttp(request).getMethod().toUpperCase(Locale.ENGLISH).equals("POST")) {
                return onLogoutRequestNotAPost(request, response);
            }
        }
        String redirectUrl = shiroProperties.getLogoutSuccessRedirectUrl();
        if (Strings.isEmpty(redirectUrl)) {
            redirectUrl = getRedirectUrl(request, response, subject);
        }
        final String redirectUrlToUse = redirectUrl;
        execute(() -> processBeforeLogoutChain(subject, request, response));
        //try/catch added for SHIRO-298:
        try {
            subject.logout();
        } catch (SessionException ise) {
            log.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }
        execute(() -> processAfterLogoutChain(subject, request, response));
        execute(() -> processBeforeLogoutRedirectChain(subject, request, response, redirectUrlToUse));
        issueRedirect(request, response, redirectUrlToUse);
        return false;
    }

    private void execute(VoidApplyFunction function) {
        try {
            function.apply();
        } catch (Exception e) {
            log.error("Execute processing logout handler method chains failed. This can generally safely be ignored", e);
        }
    }

    private void processBeforeLogoutChain(Subject subject, ServletRequest request, ServletResponse response) {
        abstractShiroLogoutFilterConfigurers.forEach(each -> each.processBeforeLogout(subject, request, response));
    }

    private void processAfterLogoutChain(Subject subject, ServletRequest request, ServletResponse response) {
        abstractShiroLogoutFilterConfigurers.forEach(each -> each.processAfterLogout(subject, request, response));
    }

    private void processBeforeLogoutRedirectChain(Subject subject, ServletRequest request, ServletResponse response, String redirectUrl) {
        abstractShiroLogoutFilterConfigurers.forEach(each -> each.processBeforeLogoutRedirect(subject, request, response, redirectUrl));
    }

    @FunctionalInterface
    interface VoidApplyFunction {

        void apply();
    }
}
