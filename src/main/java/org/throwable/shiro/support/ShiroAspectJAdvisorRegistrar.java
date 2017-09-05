package org.throwable.shiro.support;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/9/4 23:55
 */
public abstract class ShiroAspectJAdvisorRegistrar {

    public static void registerShiroLoginAspectJAdvisor(DefaultListableBeanFactory beanFactory) {
        ShiroLoginAspectJAdvisor shiroLoginAspectJAdvisor = new ShiroLoginAspectJAdvisor();
        shiroLoginAspectJAdvisor.setBeanFactory(beanFactory);
        shiroLoginAspectJAdvisor.setLocation("$$shiroLoginAspectJAdvisor##");
        shiroLoginAspectJAdvisor.setAdvice(new ShiroLoginAdvice());
        shiroLoginAspectJAdvisor.setExpression("@annotation(org.throwable.shiro.common.annotation.ShiroLogin)");
        beanFactory.registerSingleton("shiroLoginAspectJAdvisor", shiroLoginAspectJAdvisor);
    }
}
