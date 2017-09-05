package org.throwable.shiro.support.serialize;

import com.alibaba.fastjson.JSON;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/2 23:54
 */
public class FastJsonSerializer implements Serializer {

	@Override
	public byte[] serialize(Object target) {
		if (null == target) {
			return new byte[0];
		}
		return JSON.toJSONBytes(target);
	}

	@Override
	public Object deserialize(byte[] bytes) {
		if (null == bytes || 0 == bytes.length) {
			return null;
		}
		return JSON.parse(bytes);
	}
}
