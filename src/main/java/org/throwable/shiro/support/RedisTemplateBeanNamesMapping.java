package org.throwable.shiro.support;

import org.throwable.shiro.common.constants.RedisModel;
import org.throwable.shiro.common.constants.RedisTemplateBeanNames;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 18:54
 */
public abstract class RedisTemplateBeanNamesMapping implements RedisTemplateBeanNames {

    public static final Map<RedisModel, String> TEMPLATES = new EnumMap<>(RedisModel.class);

    static {
        TEMPLATES.put(RedisModel.CLUSTER, CLUSTER_TEMPLATE);
        TEMPLATES.put(RedisModel.SENTINEL, SENTINEL_TEMPLATE);
        TEMPLATES.put(RedisModel.SINGLE, SINGLE_TEMPLATE);
    }
}
