package org.throwable.shiro.support;

import org.throwable.shiro.utils.Asserts;
import org.throwable.shiro.utils.Strings;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/3 13:42
 */
public class ShiroMessageResourceBundle {

	private static final String BUNDLE_NAME = "shiro_message";
	private ResourceBundle delegate;

	public ShiroMessageResourceBundle(String locale) {
		Asserts.notEmpty(locale, "Locale to init resourceBundle must not be empty!");
		String[] splits = locale.split("_");
		initResourceBundle(splits[0], splits[1]);
	}

	private void initResourceBundle(String language, String country) {
		Locale locale;
		if (Strings.isEmpty(language)) {
			locale = Locale.getDefault();
		} else {
			locale = new Locale(language, country);
		}
		delegate = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

	public String getBundleValueByKey(String key) {
		return delegate.getString(key);
	}

	public String getBundleValueByKeyAndFormat(String key, Object... params) {
		return MessageFormat.format(getBundleValueByKey(key), params);
	}
}
