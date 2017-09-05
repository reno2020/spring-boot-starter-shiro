package org.throwable.shiro.common;

import org.apache.shiro.authc.AuthenticationException;
import org.throwable.shiro.common.constants.LoginResultType;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 12:07
 */
public class LoginFailResult<T extends AuthenticationException> extends LoginResult{

	private T authenticationException;

	public LoginFailResult(T authenticationException) {
		super(LoginResultType.FAIL);
		this.authenticationException = authenticationException;
	}

	public T getAuthenticationException() {
		return authenticationException;
	}

	public void setAuthenticationException(T authenticationException) {
		this.authenticationException = authenticationException;
	}
}
