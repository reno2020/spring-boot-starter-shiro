package org.throwable.shiro.support.serialize;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/5 0:39
 */
@Slf4j
public class DefaultShiroSerializer implements Serializer {

	private static final int DEFAULT_BUFF_SIZE = 1024;

	@Override
	public byte[] serialize(Object target) {
		byte[] result = null;
		if (null == target) {
			return new byte[0];
		} else {
			if (!(target instanceof Serializable)) {
				throw new IllegalArgumentException(DefaultShiroSerializer.class.getSimpleName() + " requires a Serializable payload "
						+ "but received an object of type [" + target.getClass().getName() + "]");
			}
			try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(DEFAULT_BUFF_SIZE);
				 ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream)) {
				objectOutputStream.writeObject(target);
				result = byteStream.toByteArray();
			} catch (Exception e) {
				log.error("Failed to serialize", e);
			}
			return result;
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		Object result = null;
		if (isEmpty(bytes)) {
			return null;
		} else {
			try (ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
				 ObjectInputStream objectInputStream = new ObjectInputStream(byteStream)) {
				result = objectInputStream.readObject();
			} catch (Exception e) {
				log.error("Failed to deserialize", e);
			}
			return result;
		}
	}

	private static boolean isEmpty(byte[] data) {
		return data == null || data.length == 0;
	}
}
