package org.throwable.shiro.exception;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 14:45
 */
public class ShiroConfigurationParseException extends RuntimeException {

	public ShiroConfigurationParseException(String message) {
		super(message);
	}

	public ShiroConfigurationParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ShiroConfigurationParseException(Throwable cause) {
		super(cause);
	}
}
