package org.throwable.shiro.support;

import org.apache.shiro.authz.Permission;
import org.throwable.shiro.common.LoginResult;

import java.util.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 11:32
 */
public class CompositeShiroRealmConfigurerParser {

    private final List<AbstractShiroRealmConfigurer> configurers = new ArrayList<>();

    public void addShiroRealmConfigurer(AbstractShiroRealmConfigurer shiroRealmConfigurer) {
        configurers.add(shiroRealmConfigurer);
    }

    public void addShiroRealmConfigurers(Collection<AbstractShiroRealmConfigurer> shiroRealmConfigurers) {
        configurers.addAll(shiroRealmConfigurers);
    }

    public void setShiroRealmConfigurers(Collection<AbstractShiroRealmConfigurer> shiroRealmConfigurers) {
        configurers.clear();
        configurers.addAll(shiroRealmConfigurers);
    }

    public Set<String> parseRoles(Object principal) {
        final Set<String> roles = new HashSet<>();
        configurers.forEach(each -> roles.addAll(each.addRoles(principal)));
        return roles;
    }

    public Set<String> parsePermissionStrings(Object principal) {
        final Set<String> permissionStrings = new HashSet<>();
        configurers.forEach(each -> permissionStrings.addAll(each.addPermissionStrings(principal)));
        return permissionStrings;
    }

    public Set<Permission> parsePermissions(Object principal) {
        final Set<Permission> permissions = new HashSet<>();
        configurers.forEach(each -> permissions.addAll(each.addPermissions(principal)));
        return permissions;
    }

    public List<LoginResult> processLoginChains(String principal, String credential) {
        final List<LoginResult> loginResults = new LinkedList<>();
        configurers.forEach(each -> loginResults.addAll(each.processLogin(principal, credential)));
        return loginResults;
    }
}
