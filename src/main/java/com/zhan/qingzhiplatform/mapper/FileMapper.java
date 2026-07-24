package com.zhan.qingzhiplatform.mapper;

import com.zhan.qingzhiplatform.pojo.entity.FileEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface FileMapper {

    @Insert("INSERT INTO files(original_name, storage_name, file_path, file_size, file_type, md5_hash, upload_user_id) " +
            "VALUES(#{originalName}, #{storageName}, #{filePath}, #{fileSize}, #{fileType}, #{md5Hash}, #{uploadUserId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(FileEntity file);

    @Select("SELECT * FROM files WHERE id = #{id}")
    FileEntity getById(Long id);

    @Select("SELECT * FROM files WHERE md5_hash = #{md5}")
    FileEntity getByMd5(String md5);

    @Delete("DELETE FROM files WHERE id = #{id}")
    void deleteById(Long id);
}
