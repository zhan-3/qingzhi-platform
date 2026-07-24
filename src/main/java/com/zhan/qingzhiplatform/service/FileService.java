package com.zhan.qingzhiplatform.service;

import com.zhan.qingzhiplatform.pojo.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    FileEntity uploadFile(MultipartFile file, Long userId);
    List<FileEntity> batchUploadFiles(List<MultipartFile> files, Long userId);
    FileEntity getFileById(Long id);
    void deleteFile(Long id);
}
