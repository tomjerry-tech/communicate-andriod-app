package com.ruoyi.tom.service;

import com.ruoyi.tom.entity.User;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    boolean checkUsernameExists(String username);
}