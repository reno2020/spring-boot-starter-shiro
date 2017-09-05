package org.throwable.shiro.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.throwable.shiro.common.LoginFailResult;
import org.throwable.shiro.common.LoginResult;
import org.throwable.shiro.common.constants.LoginResultType;
import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.support.cache.AbstractRedisTemplate;

import java.util.List;
import java.util.Set;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 11:48
 */
@Slf4j
public class SimpleShiroRealm extends AuthorizingRealm {

    private static final String REALM_NAME = "simpleShiroRealm";

    //login count
    private static final String SHIRO_LOGIN_COUNT = "shiro_login_count:";

    //login lock
    private static final String SHIRO_IS_LOCK = "shiro_login_lock:";

    //lock sign
    private static final String SHIRO_LOCK_SIGN = "LOCK";

    private CompositeShiroRealmConfigurerParser configurerParser;

    private ShiroProperties shiroProperties;

    private AbstractRedisTemplate<String, String> redisTemplate;

    private ShiroMessageResourceBundle resourceBundle;

    public SimpleShiroRealm(CompositeShiroRealmConfigurerParser configurerParser,
                            ShiroMessageResourceBundle resourceBundle,
                            ShiroProperties shiroProperties,
                            AbstractRedisTemplate<String, String> redisTemplate) {
        this.configurerParser = configurerParser;
        this.shiroProperties = shiroProperties;
        super.setName(REALM_NAME);
        this.redisTemplate = redisTemplate;
        this.resourceBundle = resourceBundle;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object principal = getAvailablePrincipal(principals);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roles = configurerParser.parseRoles(principal);
        if (!CollectionUtils.isEmpty(roles)) {
            authorizationInfo.setRoles(roles);
        }
        Set<String> permissionStrings = configurerParser.parsePermissionStrings(principal);
        if (!CollectionUtils.isEmpty(permissionStrings)) {
            authorizationInfo.setStringPermissions(permissionStrings);
        }
        Set<Permission> permissions = configurerParser.parsePermissions(principal);
        if (!CollectionUtils.isEmpty(permissions)) {
            authorizationInfo.addObjectPermissions(permissions);
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        char[] passwordChars = usernamePasswordToken.getPassword();
        processLoginLockIfNecessary(username);
        String password;
        if (null != passwordChars && 0 < passwordChars.length) {
            password = new String(usernamePasswordToken.getPassword());
        } else {
            password = null;
        }
        if (log.isDebugEnabled()) {
            log.debug("Logging Current login user authentication info --> principal:{},credential:{}", username, password);
        }
        handleLoginResults(configurerParser.processLoginChains(username, password));
        processLoginLockCountResetIfNecessary(username);
        return new SimpleAuthenticationInfo(username, password, getName());
    }

    private void processLoginLockIfNecessary(String username) {
        if (shiroProperties.getEnableLoginLock()) {
            Integer maxLoginFailAttempts = shiroProperties.getMaxLoginFailAttempts();
            if (SHIRO_LOCK_SIGN.equals(redisTemplate.getValueByKey(SHIRO_IS_LOCK + username))) {
                throw new ExcessiveAttemptsException(resourceBundle.getBundleValueByKeyAndFormat("ExcessiveAttemptsException", maxLoginFailAttempts));
            }
            redisTemplate.increment(SHIRO_LOGIN_COUNT + username, 1);
            if (Integer.parseInt(redisTemplate.getValueByKey(SHIRO_LOGIN_COUNT + username)) >= maxLoginFailAttempts) {
                redisTemplate.putValueByKey(SHIRO_IS_LOCK + username, SHIRO_LOCK_SIGN, shiroProperties.getLoginLockSeconds());
            }
        }
    }

    private void processLoginLockCountResetIfNecessary(String username) {
        if (shiroProperties.getEnableLoginLock()) {
            redisTemplate.putValueByKey(SHIRO_LOGIN_COUNT + username, "0", shiroProperties.getLoginLockSeconds());
        }
    }

    private void handleLoginResults(List<LoginResult> loginResults) {
        loginResults.forEach(result -> {
            if (LoginResultType.FAIL.equals(result.getLoginResultType())) {
                LoginFailResult failResult = (LoginFailResult) result;
                throw failResult.getAuthenticationException();
            }
        });
    }
}
