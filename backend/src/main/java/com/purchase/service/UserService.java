package com.purchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.purchase.entity.User;

public interface UserService extends IService<User> {
    
    User getByUsername(String username);
    
}