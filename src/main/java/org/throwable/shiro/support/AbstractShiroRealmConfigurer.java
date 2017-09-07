package org.throwable.shiro.support;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.throwable.shiro.common.LoginResult;
import org.throwable.shiro.common.LoginSuccessResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 11:31
 */
public abstract class AbstractShiroRealmConfigurer {

	protected AuthorizationInfo processAuthorizationInfo(String principal, PrincipalCollection principals) {
		return null;
	}

	protected List<LoginResult> processLogin(String principal, String credential) {
		return new ArrayList<>(Collections.singletonList(new LoginSuccessResult()));
	}
}
