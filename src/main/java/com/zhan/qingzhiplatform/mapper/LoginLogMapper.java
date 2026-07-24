package com.zhan.qingzhiplatform.mapper;

import com.zhan.qingzhiplatform.pojo.entity.LoginLogEntity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface LoginLogMapper {

    @Insert("INSERT INTO login_logs(user_id, ip_address, login_time, success) " +
            "VALUES(#{userId}, #{ipAddress}, #{loginTime}, #{success})")
    void insert(LoginLogEntity log);

    @Select("SELECT COUNT(*) FROM login_logs WHERE user_id = #{userId} " +
            "AND login_time >= #{since} AND success = 0")
    int countFailures(@Param("userId") Long userId, @Param("since") LocalDateTime
            since);

}
