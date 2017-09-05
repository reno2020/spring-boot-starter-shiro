package org.throwable.shiro.utils;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 23:24
 */
public abstract class Strings {

	public static boolean isNotEmpty(String target) {
		return !isEmpty(target);
	}

	public static boolean isEmpty(String target) {
		return null == target || "".equals(target);
	}
}
