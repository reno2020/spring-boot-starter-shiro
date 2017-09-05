package org.throwable.shiro.support;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.throwable.shiro.common.annotation.ShiroLogin;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/4 0:41
 */
public class ShiroLoginAdvice implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object result = invocation.proceed();
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		Method method = invocation.getMethod();
		ShiroLogin annotation = AnnotationUtils.findAnnotation(method, ShiroLogin.class);
		if (null != annotation) {
			String username = WebUtils.getCleanParam(request, annotation.usernameParam());
			String password = WebUtils.getCleanParam(request, annotation.passwordParam());
			Boolean rememberMe = WebUtils.isTrue(request, annotation.rememberMeParam());
			UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
			SecurityUtils.getSubject().login(token);
		}
		return result;
	}
}
