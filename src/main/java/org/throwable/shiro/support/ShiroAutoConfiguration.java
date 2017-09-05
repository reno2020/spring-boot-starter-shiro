package org.throwable.shiro.support;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.*;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.ShiroEventBusBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.throwable.shiro.common.constants.RedisModel;
import org.throwable.shiro.configuration.ShiroProperties;
import org.throwable.shiro.exception.ShiroConfigurationParseException;
import org.throwable.shiro.support.cache.AbstractRedisTemplate;
import org.throwable.shiro.support.cache.ShiroRedisCacheManager;
import org.throwable.shiro.support.session.ShiroSessionDAO;
import org.throwable.shiro.utils.Asserts;
import org.throwable.shiro.utils.Strings;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author throwable
 * @version v1.0
 * @description under spring-boot projects,the configuration classes in shiro-spring packages will not be processed!
 * @since 2017/9/3 10:06
 */
@SuppressWarnings("unchecked")
@ConditionalOnClass(value = {ShiroFilterFactoryBean.class, DefaultSecurityManager.class})
@EnableConfigurationProperties(value = {ShiroProperties.class})
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ShiroAutoConfiguration implements BeanFactoryAware, InitializingBean {

    private DefaultListableBeanFactory beanFactory;
    @Autowired
    private ShiroProperties shiroProperties;
    private AbstractRedisTemplate redisTemplate;
    private AbstractRedisTemplate<String, Session> sessionRedisTemplate;
    private RedisModel redisModel;

    @Autowired(required = false)
    private RolePermissionResolver rolePermissionResolver;

    @Autowired(required = false)
    protected PermissionResolver permissionResolver;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        if (null == shiroProperties) {
            shiroProperties = processShiroPropertiesEarlyConfigure();
        }
        Asserts.notNull(shiroProperties, "Configure ShiroProperties from spring environment failed!");
        this.redisModel = RedisTemplateRegisterAssistant.resolveRedisModel(shiroProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate = registerRedisTemplate();
        sessionRedisTemplate = createSessionRedisTemplate();
        ShiroAspectJAdvisorRegistrar.registerShiroLoginAspectJAdvisor(beanFactory);
    }

    private ShiroProperties processShiroPropertiesEarlyConfigure() {
        ConfigurationPropertiesBindingPostProcessor bindingPostProcessor = beanFactory.getBean(ConfigurationPropertiesBindingPostProcessor.class);
        Asserts.notNull(bindingPostProcessor, "Fetch ConfigurationPropertiesBindingPostProcessor bean from spring context failed!");
        ShiroProperties shiroPropertiesBean = null;
        try {
            shiroPropertiesBean = beanFactory.getBean(ShiroProperties.class);
        } catch (Exception e) {
            //ignore
        }
        if (null == shiroPropertiesBean) {
            shiroPropertiesBean = new ShiroProperties();
            beanFactory.registerSingleton("shiroProperties", shiroPropertiesBean);
            shiroPropertiesBean = beanFactory.getBean("shiroProperties", ShiroProperties.class);
        }
        String propertyBeanName = ShiroProperties.PREFIX + "-" + ShiroProperties.class.getName();
        return (ShiroProperties) bindingPostProcessor.postProcessBeforeInitialization(shiroPropertiesBean, propertyBeanName);
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public EventBus eventBus() {
        return new DefaultEventBus();
    }

    @Bean
    public ShiroEventBusBeanPostProcessor shiroEventBusBeanPostProcessor(EventBus eventBus) {
        return new ShiroEventBusBeanPostProcessor(eventBus);
    }

    @Bean
    public ShiroLoggingSessionListener shiroLoggingSessionListener(){
        return new ShiroLoggingSessionListener();
    }

    @Bean
    @ConditionalOnExpression(value = "${spring.shiro.enable-shiro-realm:false}")
    public CompositeShiroRealmConfigurerParser compositeShiroRealmConfigurerParser() {
        CompositeShiroRealmConfigurerParser configurerParser = new CompositeShiroRealmConfigurerParser();
        Map<String, AbstractShiroRealmConfigurer> realmConfigurerMap = beanFactory.getBeansOfType(AbstractShiroRealmConfigurer.class);
        if (null == realmConfigurerMap || realmConfigurerMap.isEmpty()) {
            throw new ShiroConfigurationParseException("When enabled spring.shiro.enable-shiro-realm,at least one " +
                    "AbstractShiroRealmConfigurer bean must be registered into spring context!");
        } else {
            configurerParser.setShiroRealmConfigurers(realmConfigurerMap.values());
        }
        return configurerParser;
    }

    @Bean
    public ShiroMessageResourceBundle shiroMessageResourceBundle() {
        return new ShiroMessageResourceBundle(shiroProperties.getLocale());
    }

    @Bean
    @ConditionalOnExpression(value = "${spring.shiro.enable-shiro-realm:false}")
    public SimpleShiroRealm simpleShiroRealm(CompositeShiroRealmConfigurerParser compositeShiroRealmConfigurerParser,
                                             ShiroMessageResourceBundle shiroMessageResourceBundle) {
        return new SimpleShiroRealm(compositeShiroRealmConfigurerParser, shiroMessageResourceBundle, shiroProperties, createStringRedisTemplate());
    }

    @Bean
    public SessionStorageEvaluator sessionStorageEvaluator() {
        return new DefaultSessionStorageEvaluator();
    }

    @Bean
    public SubjectFactory subjectFactory() {
        return new DefaultWebSubjectFactory();
    }

    @Bean
    public SubjectDAO subjectDAO(SessionStorageEvaluator sessionStorageEvaluator) {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        return subjectDAO;
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new SimpleSessionFactory();
    }

    @Bean
    public ShiroSessionDAO shiroSessionDAO() {
        return new ShiroSessionDAO(sessionRedisTemplate, shiroProperties.getSessionTimeoutSeconds());
    }

    @Bean
    public ShiroRedisCacheManager shiroRedisCacheManager() {
        return new ShiroRedisCacheManager(redisTemplate);
    }

    @Bean
    public DefaultSessionManager sessionManager(ShiroSessionDAO shiroSessionDAO,
                                                SessionFactory sessionFactory,
                                                EventBus eventBus) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(shiroSessionDAO);
        sessionManager.setDeleteInvalidSessions(shiroProperties.getDeleteInvalidSessions());
        sessionManager.setSessionFactory(sessionFactory);
        sessionManager.setGlobalSessionTimeout(shiroProperties.getSessionTimeoutSeconds() * 1000);
        sessionManager.setSessionValidationInterval(shiroProperties.getSessionTimeoutSeconds() * 1000);
        sessionManager.getSessionIdCookie().setName(shiroProperties.getSessionCookieName());
        Map<String, SessionListener> sessionListenerMap = beanFactory.getBeansOfType(SessionListener.class);
        if (!CollectionUtils.isEmpty(sessionListenerMap)) {
            sessionManager.setSessionListeners(sessionListenerMap.values());
            sessionManager.setEventBus(eventBus);
        }
        return sessionManager;
    }

    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie(shiroProperties.getRememberMeCookieName());
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(shiroProperties.getMaxCookieAge());
        return simpleCookie;
    }

    @Bean
    public CookieRememberMeManager cookieRememberMeManager(SimpleCookie simpleCookie) throws Exception {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(simpleCookie);
        String cipherKey = shiroProperties.getCipherKey();
        if (Strings.isEmpty(cipherKey)) {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecretKey secretKey = keygen.generateKey();
            cipherKey = Base64.encodeToString(secretKey.getEncoded());
            shiroProperties.setCipherKey(cipherKey);
        }
        cookieRememberMeManager.setCipherKey(Base64.decode(cipherKey));
        return cookieRememberMeManager;
    }

    @Bean
    public AuthenticationStrategy authenticationStrategy() {
        return new AtLeastOneSuccessfulStrategy();
    }

    @Bean
    public Authenticator authenticator(AuthenticationStrategy authenticationStrategy) {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(authenticationStrategy);
        return authenticator;
    }

    @Bean
    public Authorizer authorizer() {
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
        if (null != rolePermissionResolver) {
            authorizer.setRolePermissionResolver(rolePermissionResolver);
        }
        if (null != permissionResolver) {
            authorizer.setPermissionResolver(permissionResolver);
        }
        return authorizer;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(SubjectDAO subjectDAO,
                                                     SubjectFactory subjectFactory,
                                                     CookieRememberMeManager cookieRememberMeManager,
                                                     ShiroRedisCacheManager shiroRedisCacheManager,
                                                     EventBus eventBus,
                                                     DefaultSessionManager sessionManager,
                                                     Authenticator authenticator,
                                                     Authorizer authorizer,
                                                     List<Realm> realms) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSubjectDAO(subjectDAO);
        securityManager.setSubjectFactory(subjectFactory);
        securityManager.setRememberMeManager(cookieRememberMeManager);
        securityManager.setCacheManager(shiroRedisCacheManager);
        securityManager.setEventBus(eventBus);
        securityManager.setSessionManager(sessionManager);
        securityManager.setAuthenticator(authenticator);
        securityManager.setAuthorizer(authorizer);
        securityManager.setRealms(realms);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        if (null == ThreadContext.getSecurityManager()) {
            ThreadContext.bind(securityManager);
        }
        if (null == SecurityUtils.getSecurityManager()) {
            SecurityUtils.setSecurityManager(securityManager);
        }
        shiroFilter.setSecurityManager(securityManager);
        shiroFilter.setLoginUrl(shiroProperties.getLoginUrl());
        shiroFilter.setSuccessUrl(shiroProperties.getLoginSuccessRedirectUrl());
        shiroFilter.setUnauthorizedUrl(shiroProperties.getUnauthorizedRedirectUrl());
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        //rewrite FormAuthenticationFilter
        filterMap.put("authc", new ShiroFormAuthenticationFilter(shiroProperties));
        //rewrite LogoutFilter
        Map<String, AbstractShiroLogoutFilterConfigurer> shiroLogoutFilterConfigurerMap = beanFactory.getBeansOfType(AbstractShiroLogoutFilterConfigurer.class);
        filterMap.put("logout", new ShiroLogoutFilter(shiroProperties, shiroLogoutFilterConfigurerMap.values()));
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //default shiro filter chain definition,add to the tail of filter chain
        filterChainDefinitionMap.put(shiroProperties.getLoginUrl(), "anon");
        filterChainDefinitionMap.put(shiroProperties.getUnauthorizedRedirectUrl(), "anon");
        filterChainDefinitionMap.put(shiroProperties.getLogoutUrl(), "logout");
        Map<String, AbstractShiroFilterConfigurer> shiroFilterConfigurerMap = beanFactory.getBeansOfType(AbstractShiroFilterConfigurer.class);
        if (null != shiroFilterConfigurerMap && !shiroFilterConfigurerMap.isEmpty()) {
            for (AbstractShiroFilterConfigurer configurer : shiroFilterConfigurerMap.values()) {
                Map<String, Filter> filterMapFromConfigurer = configurer.setFilters();
                if (null != filterMapFromConfigurer && !filterMapFromConfigurer.isEmpty()) {
                    filterMap.putAll(filterMapFromConfigurer);
                }
                Map<String, String> filterChainDefinitionMapFromConfigurer = configurer.setFilterChainDefinitionMap();
                if (null != filterChainDefinitionMapFromConfigurer && !filterChainDefinitionMapFromConfigurer.isEmpty()) {
                    filterChainDefinitionMap.putAll(filterChainDefinitionMapFromConfigurer);
                }
            }
            filterChainDefinitionMap.putAll(IniConfigurationParser.parseFilterChainDefinitionMapFromIni(shiroProperties.getIniFileLocation()));
        }
        if (!filterMap.isEmpty()) {
            shiroFilter.setFilters(filterMap);
        }
        //default shiro filter chain definition,add to the tail of filter chain
        if (shiroProperties.getEnableAnonymous() && !filterChainDefinitionMap.containsKey("/**")) {
            filterChainDefinitionMap.put("/**", "anon");
        }
        if (!filterChainDefinitionMap.isEmpty()) {
            shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        }
        return shiroFilter;
    }

    private AbstractRedisTemplate registerRedisTemplate() {
        AbstractRedisTemplate abstractRedisTemplate = RedisTemplateRegisterAssistant.createRedisTemplate(shiroProperties);
        String beanName = RedisTemplateBeanNamesMapping.TEMPLATES.get(redisModel);
        beanFactory.registerSingleton(beanName, abstractRedisTemplate);
        return (AbstractRedisTemplate) beanFactory.getBean(beanName);
    }

    private AbstractRedisTemplate<String, Session> createSessionRedisTemplate() {
        String beanName = RedisTemplateBeanNamesMapping.TEMPLATES.get(redisModel);
        return (AbstractRedisTemplate<String, Session>) beanFactory.getBean(beanName);
    }

    private AbstractRedisTemplate<String, String> createStringRedisTemplate() {
        String beanName = RedisTemplateBeanNamesMapping.TEMPLATES.get(redisModel);
        return (AbstractRedisTemplate<String, String>) beanFactory.getBean(beanName);
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.shiro", name = "enable-unauthorized-redirect", havingValue = "true")
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        Properties exceptionMappings = new Properties();
        String unauthorizedUrl = shiroProperties.getUnauthorizedRedirectUrl();
        if (Strings.isEmpty(unauthorizedUrl)) {
            unauthorizedUrl = ShiroProperties.UNAUTHORIZED_REDIRECT_URL;
        }
        unauthorizedUrl = unauthorizedUrl.replace("/", "");
        exceptionMappings.put(AuthorizationException.class.getCanonicalName(), unauthorizedUrl);
        exceptionMappings.put(AuthenticationException.class.getCanonicalName(), unauthorizedUrl);
        simpleMappingExceptionResolver.setExceptionMappings(exceptionMappings);
        return simpleMappingExceptionResolver;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.shiro", name = "enable-unauthorized-redirect", havingValue = "false")
    public ShiroResponseBodyExceptionHandler shiroResponseBodyExceptionHandler() {
        return new ShiroResponseBodyExceptionHandler();
    }
}
