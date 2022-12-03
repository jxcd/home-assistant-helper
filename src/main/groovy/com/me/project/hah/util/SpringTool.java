package com.me.project.hah.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
public class SpringTool implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(SpringTool.class);

    private static ApplicationContext context;

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static String getProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return context.getEnvironment().getProperty(key, defaultValue);
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return context.getEnvironment().getProperty(key, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        return context.getEnvironment().getProperty(key, targetType, defaultValue);
    }

    public static void autowireBean(Object existingBean) {
        context.getAutowireCapableBeanFactory().autowireBean(existingBean);
    }

    /**
     * 向容器中注册对象
     *
     * @param beanName        对象名, 可null但不建议null
     * @param beanClass       对象类
     * @param constructorArgs 构造器参数
     */
    public static void registerBean(String beanName, Class<?> beanClass, Object... constructorArgs) {
        log.info("register bean [{}], beanClass: {}, constructorArgs: {}", beanName, beanClass, constructorArgs);
        registerBean(beanName, applicationContext -> applicationContext.registerBean(beanName, beanClass, constructorArgs));
    }

    /**
     * 向容器中注册对象
     *
     * @param beanName     对象名, 可null但不建议null
     * @param beanClass    对象类
     * @param beanSupplier 对象提供者
     * @param <T>          泛型
     */
    public static <T> void registerBean(String beanName, Class<T> beanClass, Supplier<T> beanSupplier) {
        log.info("register bean [{}], beanClass: {}", beanName, beanClass);
        registerBean(beanName, applicationContext -> applicationContext.registerBean(beanName, beanClass, beanSupplier));
    }

    /**
     * 向容器中注册对象
     * 为了避免重复注册, 此处加锁保证安全
     *
     * @param beanName 对象名, 可null但不建议null
     * @param consumer lambda
     */
    private synchronized static void registerBean(String beanName, Consumer<GenericApplicationContext> consumer) {
        if (context.containsBean(beanName)) {
            log.info("the bean [{}] already registered", beanName);
            return;
        }

        if (context instanceof GenericApplicationContext applicationContext) {
            // 最后通过 registerBeanDefinition(String beanName, BeanDefinition beanDefinition) 注册到容器中
            consumer.accept(applicationContext);
            log.info("register bean [{}] success", beanName);
        } else {
            log.warn("not support register bean, context: {}", context);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        log.debug("init context...");
    }

}
