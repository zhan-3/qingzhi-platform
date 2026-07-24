package com.zhan.qingzhiplatform.mapper;

import com.zhan.qingzhiplatform.pojo.entity.FavoriteEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Insert("INSERT INTO favorites(user_id, resource_id) VALUES(#{userId}, #{resourceId})")
    void insert(FavoriteEntity favorite);

    @Delete("DELETE FROM favorites WHERE user_id = #{userId} AND resource_id = #{resourceId}")
    void deleteByUserAndResource(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId} AND resource_id = #{resourceId}")
    boolean exists(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<FavoriteEntity> getByUserId(Long userId);
}
