package org.throwable.shiro.utils;

import java.util.Collection;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 23:28
 */
public abstract class Asserts {

	public static void notNull(Object target, String message, Object... objects){
		if (null == target) {
			throw new IllegalArgumentException(String.format(message, objects));
		}
	}

	public static void notEmpty(String target, String message, Object... objects) {
		if (Strings.isEmpty(target)) {
			throw new IllegalArgumentException(String.format(message, objects));
		}
	}

	public static void notEmpty(Collection target, String message, Object... objects) {
		if (null == target || target.isEmpty()) {
			throw new IllegalArgumentException(String.format(message, objects));
		}
	}
}
