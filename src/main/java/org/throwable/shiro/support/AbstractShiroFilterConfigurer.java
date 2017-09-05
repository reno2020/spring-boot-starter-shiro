package org.throwable.shiro.support;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 14:32
 */
public abstract class AbstractShiroFilterConfigurer {

	protected Map<String, Filter> setFilters() {
		return new HashMap<>();
	}

	protected Map<String, String> setFilterChainDefinitionMap() {
		return new HashMap<>();
	}
}
