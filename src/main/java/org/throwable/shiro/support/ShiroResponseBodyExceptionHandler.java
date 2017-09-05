package org.throwable.shiro.support;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 23:58
 */
public class ShiroResponseBodyExceptionHandler implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
										 Object handler, Exception ex) {
		if (shouldApplyTo(ex)) {
			ModelAndView mav = new ModelAndView();
			FastJsonJsonView view = new FastJsonJsonView();
			view.addStaticAttribute("code", 403);
			view.addStaticAttribute("message", ex.getMessage());
			mav.setView(view);
			return mav;
		} else {
			return null;
		}
	}

	private boolean shouldApplyTo(Exception ex) {
		Class<? extends Exception> exClass = ex.getClass();
		return AuthorizationException.class.isAssignableFrom(exClass) ||
				AuthenticationException.class.isAssignableFrom(exClass);
	}
}
