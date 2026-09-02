package com.zhan.qingzhiplatform.mapper;

import com.zhan.qingzhiplatform.pojo.entity.FavoriteEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Insert("INSERT INTO favorites(user_id, resource_id) VALUES(#{userId}, #{resourceId})")
    void insert(FavoriteEntity favorite);

    @Update("UPDATE favorites SET deleted_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND resource_id = #{resourceId} AND deleted_at IS NULL")
    int softDeleteByUserAndResource(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    @Update("UPDATE favorites SET deleted_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND deleted_at IS NULL")
    int softDeleteByUserId(Long userId);

    @Update("UPDATE favorites SET deleted_at = CURRENT_TIMESTAMP " +
            "WHERE resource_id = #{resourceId} AND deleted_at IS NULL")
    int softDeleteByResourceId(Long resourceId);

    @Update("UPDATE favorites f JOIN resources r ON f.resource_id = r.id " +
            "SET f.deleted_at = CURRENT_TIMESTAMP " +
            "WHERE r.user_id = #{userId} AND f.deleted_at IS NULL")
    int softDeleteByResourceOwner(Long userId);

    @Update("UPDATE favorites SET deleted_at = NULL, created_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND resource_id = #{resourceId} AND deleted_at IS NOT NULL")
    int restore(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId} " +
            "AND resource_id = #{resourceId} AND deleted_at IS NULL")
    boolean exists(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    @Select("SELECT f.* FROM favorites f " +
            "JOIN resources r ON f.resource_id = r.id " +
            "WHERE f.user_id = #{userId} AND f.deleted_at IS NULL " +
            "AND r.deleted_at IS NULL AND r.status = 1 ORDER BY f.created_at DESC")
    List<FavoriteEntity> getByUserId(Long userId);
}
