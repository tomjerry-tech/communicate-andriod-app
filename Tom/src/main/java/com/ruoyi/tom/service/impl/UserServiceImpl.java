package com.ruoyi.tom.service.impl;

import com.ruoyi.tom.entity.User;
import com.ruoyi.tom.repository.UserRepository;
import com.ruoyi.tom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 设置创建时间
        user.setCreateTime(new Date());
        
        // 实际项目中应该对密码进行加密
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 实际项目中应该验证加密后的密码
        // if (!passwordEncoder.matches(password, user.getPassword())) {
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        
        return user;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}