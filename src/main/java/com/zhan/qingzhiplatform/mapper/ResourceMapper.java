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

    @Delete("DELETE FROM resources WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM resources WHERE id = #{id}")
    ResourceEntity getById(Long id);

    void update(ResourceEntity resource);


    List<ResourceEntity> getResources(@Param("begin") String begin, @Param("end") String end,
                                      @Param("status") Integer status,
                                      @Param("userId") Long userId, @Param("isAdmin") boolean isAdmin);
}
