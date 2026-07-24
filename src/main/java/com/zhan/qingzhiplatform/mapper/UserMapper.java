package com.zhan.qingzhiplatform.mapper;


import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users(username, password, name, phone, email, department, major, role, status) " +
            "VALUES(#{username}, #{password}, #{name}, #{phone}, #{email}, #{department}, #{major}, #{role}, #{status})")
    void insert(UserEntity user);

    @Delete("delete from users WHERE id = #{id}")
    void deleteById(Long id);

    void update(UserEntity user);

    @Update("UPDATE users SET password=#{password} WHERE id=#{id}")
    void updatePassword(@Param("id") Long id, @Param("password") String password);

    // 查询所有用户信息
    @Select("SELECT * FROM users")
    List<UserEntity> getUsers();


    @Select("SELECT * FROM users WHERE username = #{username}")
    UserEntity getByUsername(String username);


    @Select("SELECT * FROM users WHERE id = #{id}")
    UserEntity getById(Long id);


    @Select("SELECT count(*) FROM users WHERE username = #{username}")
    boolean existsByUsername(String username);
}
