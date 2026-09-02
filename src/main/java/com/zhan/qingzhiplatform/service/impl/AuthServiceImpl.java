package com.zhan.qingzhiplatform.service.impl;

import com.zhan.qingzhiplatform.pojo.entity.LoginLogEntity;
import com.zhan.qingzhiplatform.pojo.dto.LoginDTO;
import com.zhan.qingzhiplatform.pojo.dto.RegisterDTO;
import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.LoginLogMapper;
import com.zhan.qingzhiplatform.mapper.UserMapper;
import com.zhan.qingzhiplatform.service.AuthService;
import com.zhan.qingzhiplatform.service.UserService;
import com.zhan.qingzhiplatform.util.JwtUtils;
import com.zhan.qingzhiplatform.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 用户注册
     *
     * @param dto 注册参数
     */
    @Override
    public void register(RegisterDTO dto) {
        if (dto.getRole() != null && dto.getRole() == 2) {
            throw new BusinessException("角色非法");
        }
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setDepartment(dto.getDepartment());
        user.setMajor(dto.getMajor());
        user.setRole(dto.getRole());
        userService.createUser(user);
    }


    /**
     * 用户登录
     *
     * @param dto 用户名和密码
     * @return JWT token
     */

    public String login(LoginDTO dto) {
        UserEntity u =  userMapper.getByUsername(dto.getUsername());
        // 检查账号是否存在
        if(u == null){
            throw new BusinessException("用户名或密码错误");
        }
        // 检查账号状态
        if(u.getStatus() != null && u.getStatus() == 0){
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 检查是否被锁定
        LocalDateTime fifteenMinAgo = LocalDateTime.now().minusMinutes(15);
        int failCount = loginLogMapper.countFailures(u.getId(), fifteenMinAgo);
        if (failCount >= 5) {
            throw new BusinessException("登录失败次数过多, 账号已被锁定15分钟");
        }

        // 输入密码错误计数
        if (!SecurityUtils.matches(dto.getPassword(), u.getPassword())) {
            int remain = 5 - failCount - 1;
            // 登录失败日志
            LoginLogEntity failLog = new LoginLogEntity();
            failLog.setUserId(u.getId());
            failLog.setLoginTime(LocalDateTime.now());
            failLog.setSuccess(0);
            loginLogMapper.insert(failLog);
            throw new BusinessException("密码错误，剩余尝试次数: " + Math.max(0, remain));
        }

        // 登录存载荷到token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", u.getUsername());
        claims.put("userId", u.getId());
        claims.put("role", u.getRole());

        // 记录登录日志
        LoginLogEntity log = new LoginLogEntity();
        log.setUserId(u.getId());
        log.setLoginTime(LocalDateTime.now());
        log.setSuccess(1);
        loginLogMapper.insert(log);

        return jwtUtils.generateJwt(claims);
    }
}
