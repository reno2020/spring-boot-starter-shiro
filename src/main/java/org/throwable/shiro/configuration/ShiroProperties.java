package org.throwable.shiro.configuration;

import lombok.Data;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 11:57
 */
@ConfigurationProperties(prefix = ShiroProperties.PREFIX)
@Data
public class ShiroProperties {

    public static final String PREFIX = "spring.shiro";
    public static final String UNAUTHORIZED_REDIRECT_URL = "/unauthorized";
    public static final String LOGIN_REDIRECT_URL = "/login";
    public static final String LOGOUT_REDIRECT_URL = "/logout";
    public static final String LOGIN_SUCCESS_REDIRECT_URL = "/";
    public static final String SESSION_COOKIE_NAME = "SHIRO-SESSIONID";
    public static final String REMEMBERME_COOKIE_NAME = "SHIRO-REMEMBERME";

    /**
     * (1) single node : Redis[host:port]
     * (2) sentinel nodes : Sentinel[masterName][host1:port1,host2:port2...]
     * (3) cluster nodes : Cluster[host1:port1,host2:port2...]
     */
    private String redisNodes;
    private String redisPassword;
    private Integer redisTimeout = 3000;
    private Integer redisMaxAttempts = 5;
    private Integer redisPoolMaxIdle = 8;
    private Integer redisPoolMinIdle = 0;
    private Integer redisPoolMaxActive = 8;
    //millisecond
    private Integer redisPoolMaxWait = -1;
    private Integer redisDatabase = 0;

    //session timeout 60 minutes
    private Integer sessionTimeoutSeconds = 1800;

    //unauthorized case to redirect to unauthorizedRedirectUrl
    private Boolean enableUnauthorizedRedirect = false;
    private String unauthorizedRedirectUrl = UNAUTHORIZED_REDIRECT_URL;

    private String loginUrl = LOGIN_REDIRECT_URL;
    private String logoutUrl = LOGOUT_REDIRECT_URL;
    private String loginSuccessRedirectUrl = LOGIN_SUCCESS_REDIRECT_URL;
    private String logoutSuccessRedirectUrl = LOGIN_REDIRECT_URL;

    //enable login lock
    private Boolean enableLoginLock = false;
    //only effects when enableLoginLock is true
    private Integer maxLoginFailAttempts = 5;
    //only effects when enableLoginLock is true
    private Integer LoginLockSeconds = 60 * 60 * 24;

    //zh_CN
    private String locale = "zh_CN";

    //ini file,only [urls] effects
    private String iniFileLocation;

    //enable SimpleShiroRealm to register as a spring bean
    private Boolean enableShiroRealm = false;

    private String passwordParam = FormAuthenticationFilter.DEFAULT_PASSWORD_PARAM;
    private String usernameParam = FormAuthenticationFilter.DEFAULT_USERNAME_PARAM;
    private String rememberMeParam = FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM;

    //avoid conflict for cookie name of jetty,tomcat...
    private String sessionCookieName = SESSION_COOKIE_NAME;
    private String rememberMeCookieName = REMEMBERME_COOKIE_NAME;
    //30 days
    private Integer maxCookieAge = 60 * 60 * 24 * 30;
    private String cipherKey;
    private Boolean deleteInvalidSessions = true;

    //enable anon to /*
    private Boolean enableAnonymous = true;

}
