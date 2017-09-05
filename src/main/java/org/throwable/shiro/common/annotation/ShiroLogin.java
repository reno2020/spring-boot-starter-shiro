package org.throwable.shiro.common.annotation;

import java.lang.annotation.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/4 0:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface ShiroLogin {

	String usernameParam() default "username";

	String passwordParam() default "password";

	String rememberMeParam() default "rememberMe";

}
