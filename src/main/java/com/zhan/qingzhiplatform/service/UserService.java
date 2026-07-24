package com.zhan.qingzhiplatform.service;

import com.zhan.qingzhiplatform.pojo.ImportResult;
import com.zhan.qingzhiplatform.pojo.PageResult;
import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    void createUser(UserEntity user);
    ImportResult batchImportUsers(MultipartFile file);
    void deleteUser(Long id);
    void updateUser(UserEntity user);
    void resetUserPassword(Long id, String newPassword);
    UserEntity getUserById(Long id);
    PageResult<UserEntity> listUsers(Integer page, Integer pageSize);
    void updateUserProfile(Long id, String name, String phone, String email, String department, String major);
    void changeUserPassword(Long id, String oldPassword, String newPassword);
}
