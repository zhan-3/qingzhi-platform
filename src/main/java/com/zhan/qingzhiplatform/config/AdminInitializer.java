package com.zhan.qingzhiplatform.config;

import com.zhan.qingzhiplatform.mapper.UserMapper;
import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
import com.zhan.qingzhiplatform.util.SecurityUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String @NonNull ... args) {
        if (userMapper.getByUsername("Admin") == null) {
            UserEntity admin = new UserEntity();
            admin.setUsername("Admin");
            admin.setPassword(SecurityUtils.encode("Admin2026"));
            admin.setName("系统管理员");
            admin.setRole(2);
            admin.setStatus(1);
            userMapper.insert(admin);
        }
    }
}
