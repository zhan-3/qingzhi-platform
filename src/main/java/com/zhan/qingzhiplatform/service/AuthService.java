package com.zhan.qingzhiplatform.service;

import com.zhan.qingzhiplatform.pojo.dto.LoginDTO;
import com.zhan.qingzhiplatform.pojo.dto.RegisterDTO;

public interface AuthService {

    void register(RegisterDTO dto);

    String login(LoginDTO dto);
}
