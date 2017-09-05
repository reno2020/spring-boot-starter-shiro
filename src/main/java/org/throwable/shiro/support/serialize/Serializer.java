package org.throwable.shiro.support.serialize;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 23:52
 */
public interface Serializer {

	byte[] serialize(Object target);

	Object deserialize(byte[] bytes);
}
