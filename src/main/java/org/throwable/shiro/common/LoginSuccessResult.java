package org.throwable.shiro.common;

import org.throwable.shiro.common.constants.LoginResultType;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 12:07
 */
public class LoginSuccessResult extends LoginResult{

	public LoginSuccessResult() {
		super(LoginResultType.SUCCESS);
	}
}
