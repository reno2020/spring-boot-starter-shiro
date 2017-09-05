package org.throwable.shiro.support;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.throwable.shiro.configuration.ShiroProperties;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 15:12
 */
public class ShiroFormAuthenticationFilter extends FormAuthenticationFilter {

	public ShiroFormAuthenticationFilter(ShiroProperties shiroProperties){
		setLoginUrl(shiroProperties.getLoginUrl());
		setSuccessUrl(shiroProperties.getLoginSuccessRedirectUrl());
		setPasswordParam(shiroProperties.getPasswordParam());
		setUsernameParam(shiroProperties.getUsernameParam());
		setRememberMeParam(shiroProperties.getRememberMeParam());
	}
}
