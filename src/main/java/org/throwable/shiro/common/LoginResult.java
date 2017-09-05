package org.throwable.shiro.common;

import org.throwable.shiro.common.constants.LoginResultType;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 12:05
 */
public class LoginResult {

	private LoginResultType loginResultType;

	public LoginResult(LoginResultType loginResultType) {
		this.loginResultType = loginResultType;
	}

	public LoginResultType getLoginResultType() {
		return loginResultType;
	}

	public void setLoginResultType(LoginResultType loginResultType) {
		this.loginResultType = loginResultType;
	}
}
