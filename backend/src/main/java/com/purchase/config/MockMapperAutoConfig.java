package com.purchase.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * mock模式下自动扫描Mapper接口并注册JDK Proxy bean。
 * 新增Mapper无需手动添加mock bean。
 */
@Profile("mock")
@Configuration
public class MockMapperAutoConfig implements BeanDefinitionRegistryPostProcessor {

    private static final String MAPPER_PACKAGE = "com.purchase.mapper";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        try {
            for (String className : scanMapperClassNames()) {
                Class<?> mapperClass = Class.forName(className);
                if (registry.containsBeanDefinition(mapperClass.getName())) {
                    continue;
                }
                GenericBeanDefinition bd = new GenericBeanDefinition();
                bd.setBeanClass(mapperClass);
                bd.setInstanceSupplier(() -> createMapperProxy(mapperClass));
                registry.registerBeanDefinition(mapperClass.getName(), bd);
            }
        } catch (Exception e) {
            throw new IllegalStateException("MockMapperAutoConfig自动注册Mapper失败", e);
        }
    }

    @Override
    public void postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory beanFactory) {
        // no-op
    }

    private List<String> scanMapperClassNames() throws Exception {
        List<String> classNames = new ArrayList<>();
        var classLoader = Thread.currentThread().getContextClassLoader();
        var path = MAPPER_PACKAGE.replace('.', '/');
        var resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            var url = resources.nextElement();
            if (!"file".equals(url.getProtocol())) {
                continue;
            }
            scanDirectory(new java.io.File(url.toURI()), MAPPER_PACKAGE, classNames);
        }
        return classNames;
    }

    private void scanDirectory(java.io.File dir, String packageName, List<String> classNames) {
        var files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (var file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classNames);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isInterface() && BaseMapper.class.isAssignableFrom(clazz)) {
                        classNames.add(className);
                    }
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T createMapperProxy(Class<T> mapperClass) {
        return (T) Proxy.newProxyInstance(
                mapperClass.getClassLoader(),
                new Class[]{mapperClass},
                (proxy, method, args) -> {
                    String name = method.getName();
                    Class<?> returnType = method.getReturnType();
                    if ("selectList".equals(name) || "selectPage".equals(name)) {
                        return Collections.emptyList();
                    }
                    if ("selectOne".equals(name) || "selectById".equals(name)) {
                        return null;
                    }
                    if ("selectCount".equals(name)) {
                        return 0L;
                    }
                    if ("insert".equals(name) || "update".equals(name) || "delete".equals(name)) {
                        return (returnType == int.class || returnType == Integer.class) ? 0 : null;
                    }
                    if (returnType == boolean.class || returnType == Boolean.class) {
                        return false;
                    }
                    if (returnType.isPrimitive()) {
                        return 0;
                    }
                    return null;
                }
        );
    }
}
