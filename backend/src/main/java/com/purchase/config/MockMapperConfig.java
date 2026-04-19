package com.purchase.config;

import com.purchase.entity.Category;
import com.purchase.entity.User;
import com.purchase.service.CategoryService;
import com.purchase.service.UserService;
import com.purchase.vo.CategoryTreeVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Collections;
import java.util.List;

/**
 * mock模式下需要业务逻辑mock的Service实现。
 * Mapper bean由MockMapperAutoConfig自动注册，无需手动添加。
 */
@Profile("mock")
@Configuration
public class MockMapperConfig {

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
                    user.setPassword("$2a$10$M81rJ.LMwhGzM3VXFEM9d.14O3EgTCqw9j50TRba/UbqT0qxGyuRu");
                    user.setRealName("管理员");
                    user.setStatus(1);
                    return user;
                }
                return null;
            }
        };
    }

    @Primary
    @Bean
    public CategoryService categoryService() {
        return new CategoryService() {
            @Override
            public List<CategoryTreeVO> categoryTree() {
                return Collections.emptyList();
            }

            @Override
            public List<String> findDescendantNos(String categoryNo) {
                if (categoryNo == null || categoryNo.isEmpty()) {
                    return List.of();
                }
                return List.of(categoryNo);
            }

            @Override
            public Category getCategoryByNo(String categoryNo) {
                return null;
            }

            @Override
            public Category getActiveCategoryByNo(String categoryNo) {
                return null;
            }
        };
    }
}
