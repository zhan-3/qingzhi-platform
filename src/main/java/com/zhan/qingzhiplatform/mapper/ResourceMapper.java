package com.zhan.qingzhiplatform.mapper;

import com.zhan.qingzhiplatform.pojo.entity.ResourceEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResourceMapper {

    @Insert("INSERT INTO resources(title, description, course, file_id, user_id, status) " +
            "VALUES(#{title}, #{description}, #{course}, #{fileId}, #{userId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ResourceEntity resource);

    @Update("UPDATE resources SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = #{id} AND deleted_at IS NULL")
    int softDeleteById(Long id);

    @Update("UPDATE resources SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND deleted_at IS NULL")
    void softDeleteByUserId(Long userId);

    @Select("SELECT * FROM resources WHERE id = #{id} AND deleted_at IS NULL")
    ResourceEntity getById(Long id);

    @Select("SELECT COUNT(*) > 0 FROM resources WHERE file_id = #{fileId} AND deleted_at IS NULL")
    boolean existsByFileId(Long fileId);

    @Select("SELECT COUNT(*) > 0 FROM resources " +
            "WHERE file_id = #{fileId} AND deleted_at IS NULL AND (status = 1 OR user_id = #{userId})")
    boolean existsPreviewableByFileId(@Param("fileId") Long fileId,
                                      @Param("userId") Long userId);

    void update(ResourceEntity resource);

    @Update("UPDATE resources SET status = #{status}, reject_reason = #{reason}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND deleted_at IS NULL")
    int updateAuditStatus(@Param("id") Long id,
                          @Param("status") Integer status,
                          @Param("reason") String reason);

    List<ResourceEntity> getResources(@Param("begin") String begin, @Param("end") String end,
                                      @Param("status") Integer status,
                                      @Param("userId") Long userId, @Param("isAdmin") boolean isAdmin);
}
