package org.throwable.shiro.support;

import org.apache.shiro.config.Ini;
import org.springframework.core.io.ClassPathResource;
import org.throwable.shiro.utils.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 14:58
 */
public abstract class IniConfigurationParser {

	public static Map<String, String> parseFilterChainDefinitionMapFromIni(String location) {
		Map<String, String> result = new HashMap<>();
		if (Strings.isNotEmpty(location)) {
			if (location.startsWith("classpath:")) {
				location = location.replace("classpath:", "");
			}
			if (location.startsWith("classpath*:")) {
				location = location.replace("classpath*:", "");
			}
			ClassPathResource resource = new ClassPathResource(location);
			if (resource.exists()) {
				Ini ini = new Ini();
				try {
					ini.load(resource.getInputStream());
					result.putAll(ini.getSection("urls"));
				} catch (IOException e) {
					//ignore
				}
			}
		}
		return result;
	}
}
