package org.throwable.shiro.support;

import org.apache.shiro.authz.Permission;
import org.throwable.shiro.common.LoginResult;
import org.throwable.shiro.common.LoginSuccessResult;

import java.util.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 11:31
 */
public abstract class AbstractShiroRealmConfigurer {

    protected Set<String> addRoles(Object principal) {
        return new HashSet<>();
    }

    protected Set<String> addPermissionStrings(Object principal) {
        return new HashSet<>();
    }

    protected Set<Permission> addPermissions(Object principal) {
        return new HashSet<>();
    }

    protected List<LoginResult> processLogin(String principal, String credential) {
        return new ArrayList<>(Collections.singletonList(new LoginSuccessResult()));
    }
}
