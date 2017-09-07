package org.throwable.shiro.support;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
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

	public AuthorizationInfo processCompositeAuthorizationInfoChains(String principal, PrincipalCollection principals) {
		List<AuthorizationInfo> chains = processAuthorizationInfoChains(principal, principals);
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		Set<String> roles = new HashSet<>();
		Set<String> stringPermissions = new HashSet<>();
		Set<Permission> objectPermissions = new HashSet<>();
		if (!CollectionUtils.isEmpty(chains)) {
			chains.forEach(each -> {
				if (null != each) {
					roles.addAll(each.getRoles());
					stringPermissions.addAll(each.getStringPermissions());
					objectPermissions.addAll(each.getObjectPermissions());
				}
			});
		}
		simpleAuthorizationInfo.setRoles(roles);
		simpleAuthorizationInfo.setObjectPermissions(objectPermissions);
		simpleAuthorizationInfo.setStringPermissions(stringPermissions);
		return simpleAuthorizationInfo;
	}

	private List<AuthorizationInfo> processAuthorizationInfoChains(String principal, PrincipalCollection principals) {
		final List<AuthorizationInfo> authorizationInfos = new LinkedList<>();
		configurers.forEach(each -> {
			AuthorizationInfo authorizationInfo = each.processAuthorizationInfo(principal, principals);
			if (null != authorizationInfo) {
				authorizationInfos.add(authorizationInfo);
			}
		});
		return authorizationInfos;
	}

	public List<LoginResult> processLoginChains(String principal, String credential) {
		final List<LoginResult> loginResults = new LinkedList<>();
		configurers.forEach(each -> {
			List<LoginResult> eachResults = each.processLogin(principal, credential);
			if (!CollectionUtils.isEmpty(eachResults)) {
				loginResults.addAll(eachResults);
			}
		});
		return loginResults;
	}
}
