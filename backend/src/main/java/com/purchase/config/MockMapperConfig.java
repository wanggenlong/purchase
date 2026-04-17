package com.purchase.config;

import com.purchase.entity.Product;
import com.purchase.entity.PurchaseOrder;
import com.purchase.entity.User;
import com.purchase.mapper.ProductMapper;
import com.purchase.mapper.PurchaseOrderMapper;
import com.purchase.mapper.UserMapper;
import com.purchase.service.UserService;
import com.purchase.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.reflect.Proxy;
import java.util.Collections;

@Profile("mock")
@Configuration
public class MockMapperConfig {

    @Bean
    public ProductMapper productMapper() {
        return (ProductMapper) Proxy.newProxyInstance(
                ProductMapper.class.getClassLoader(),
                new Class[]{ProductMapper.class},
                (proxy, method, args) -> {
                    if ("selectList".equals(method.getName())) {
                        return Collections.<Product>emptyList();
                    }
                    throw new UnsupportedOperationException("mock profile productMapper only supports selectList placeholder");
                }
        );
    }

    @Bean
    public PurchaseOrderMapper purchaseOrderMapper() {
        return (PurchaseOrderMapper) Proxy.newProxyInstance(
                PurchaseOrderMapper.class.getClassLoader(),
                new Class[]{PurchaseOrderMapper.class},
                (proxy, method, args) -> {
                    if ("selectList".equals(method.getName())) {
                        return Collections.<PurchaseOrder>emptyList();
                    }
                    throw new UnsupportedOperationException("mock profile purchaseOrderMapper only supports selectList placeholder");
                }
        );
    }

    @Bean
    public UserMapper userMapper() {
        return (UserMapper) Proxy.newProxyInstance(
                UserMapper.class.getClassLoader(),
                new Class[]{UserMapper.class},
                (proxy, method, args) -> {
                    if ("selectList".equals(method.getName())) {
                        return Collections.<User>emptyList();
                    }
                    if ("selectOne".equals(method.getName())) {
                        return null;
                    }
                    throw new UnsupportedOperationException("mock profile userMapper only supports selectList placeholder");
                }
        );
    }

    @Primary
    @Bean
    public UserService userService() {
        return new com.purchase.service.impl.UserServiceImpl() {
            @Override
            public User getByUsername(String username) {
                if ("admin".equals(username)) {
                    User user = new User();
                    user.setId(1L);
                    user.setUsername("admin");
                    user.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOtl7gQTzfDOo2");
                    user.setRealName("管理员");
                    user.setStatus(1);
                    return user;
                }
                return null;
            }
        };
    }
}
