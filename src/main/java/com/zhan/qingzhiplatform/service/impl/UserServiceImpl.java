package com.zhan.qingzhiplatform.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhan.qingzhiplatform.pojo.ImportResult;
import com.zhan.qingzhiplatform.pojo.PageResult;
import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
import com.zhan.qingzhiplatform.pojo.dto.UserImportDTO;
import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.UserMapper;
import com.zhan.qingzhiplatform.service.UserService;
import com.zhan.qingzhiplatform.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 创建用户
     *
     * @param user 用户实体
     */
    @Override
    public void createUser(UserEntity user) {
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new BusinessException("USER_EXISTS", "账号已存在, 请直接登录");
        }
        if (!SecurityUtils.isValidPassword(user.getPassword())) {
            throw new BusinessException("密码至少8位，包含数字和字母");
        }
        user.setPassword(SecurityUtils.encode(user.getPassword()));
        user.setStatus(1);
        userMapper.insert(user);
        log.info("创建用户成功: username={}, role={}", user.getUsername(), user.getRole());
    }

    /**
     * 批量导入注册用户 (事务回滚)
     *
     * @param file 用户excel文件
     * @return 导入结果
     */
    @Override
    @Transactional
    public ImportResult batchImportUsers(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException("请上传Excel文件(.xlsx或.xls)");
        }

        ImportResult result = new ImportResult();
        List<UserEntity> validUsers = new ArrayList<>();

        // 调用EasyExcel方法
        try {
            EasyExcel.read(file.getInputStream(), UserImportDTO.class, new AnalysisEventListener<UserImportDTO>() {
                @Override
                public void invoke(UserImportDTO dto, AnalysisContext ctx) {
                    try {
                        validUsers.add(toUser(dto));
                        result.addSuccess();
                    } catch (BusinessException e) {
                        int row = ctx.readRowHolder().getRowIndex() + 1;
                        result.addFail("第" + row + "行: " + e.getMessage());
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext ctx) {}
            }).sheet().doRead();
        } catch (Exception e) {
            throw new BusinessException("文件解析失败，请检查Excel格式");
        }

        if (result.getFail() > 0) {
            throw new BusinessException("导入失败，共" + result.getFail() + "条错误，已全部回滚:\n"
                    + String.join("\n", result.getErrors()));
        }

        for (UserEntity user : validUsers) {
            createUser(user);
        }
        return result;
    }

    /**
     * 导入类转换为User类
     *
     * @param dto 导入用户DTO
     * @return User类
     */
    private UserEntity toUser(UserImportDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank())
            throw new BusinessException("学号/工号不能为空");
        if (dto.getName() == null || dto.getName().isBlank())
            throw new BusinessException("姓名不能为空");
        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new BusinessException("密码不能为空");

        int role;
        if ("学生".equals(dto.getRole())) role = 0;
        else if ("教师".equals(dto.getRole())) role = 1;
        else throw new BusinessException("角色只能填写'学生'或'教师'");

        UserEntity user = new UserEntity();
        user.setUsername((dto.getUsername().trim()));
        user.setName(dto.getName().trim());
        user.setPassword(dto.getPassword().trim());
        user.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);
        user.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        user.setDepartment(dto.getDepartment() != null ? dto.getDepartment().trim() : null);
        user.setRole(role);
        return user;
    }

    /**
     * 删除用户
     * @param id 用户ID
     */
    @Override
    public void deleteUser(Long id) { userMapper.deleteById(id); }

    /**
     * 分页查询用户
     * @param page 当前页码
     * @param pageSize 每页条数
     * @return 查询结果
     */
    @Override
    public PageResult<UserEntity> listUsers(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        PageInfo<UserEntity> info = new PageInfo<>(userMapper.getUsers());
        return new PageResult<>(info.getList(), info.getTotal(), page, pageSize);
    }

    /**
     * 查询用户详细信息
     *
     * @param id 用户ID
     * @return 用户实体
     */
    @Override
    public UserEntity getUserById(Long id) {
        UserEntity user = userMapper.getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        return user;
    }

    /**
     * 更新个人信息
     *
     * @param id 用户ID
     * @param name 姓名
     * @param phone 电话
     * @param email 邮箱
     * @param department 部门
     * @param major 科目
     */
    @Override
    public void updateUserProfile(Long id, String name, String phone, String email, String department, String major) {
        UserEntity user = userMapper.getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setDepartment(department);
        user.setMajor(major);
        userMapper.update(user);
    }

    /**
     * 个人更改密码
     *
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    public void changeUserPassword(Long id, String oldPassword, String newPassword) {
        UserEntity user = userMapper.getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        if (!SecurityUtils.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        if (!SecurityUtils.isValidPassword(newPassword)) {
            throw new BusinessException("密码至少8位，包含数字和字母");
        }
        userMapper.updatePassword(id, SecurityUtils.encode(newPassword));
    }

    /**
     * 管理员更改用户信息
     *
     * @param user 用户实体
     */
    @Override
    public void updateUser(UserEntity user) {
        if (userMapper.getById(user.getId()) == null) throw new BusinessException("用户不存在");
        if (user.getRole() != null && user.getRole() == 2) throw new BusinessException("无法设置为管理员角色");
        userMapper.update(user);
    }

    /**
     * 管理员重置用户密码
     * @param id 用户ID
     * @param newPassword 新密码
     */
    @Override
    public void resetUserPassword(Long id, String newPassword) {
        if (!SecurityUtils.isValidPassword(newPassword)) throw new BusinessException("密码至少8位，包含数字和字母");
        if (userMapper.getById(id) == null) throw new BusinessException("用户不存在");
        userMapper.updatePassword(id, SecurityUtils.encode(newPassword));
    }
}
