package com.zhan.qingzhiplatform.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件实体类
 * 存储上传文件信息，支持MD5秒传去重
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    /**
     * 文件ID
     */
    private Long id;
    /**
     * 原始文件名
     */
    private String originalName;
    /**
     * 存储文件名（UUID）
     */
    private String storageName;
    /**
     * 文件存储路径
     */
    private String filePath;
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    /**
     * 文件MIME类型
     */
    private String fileType;
    /**
     * MD5哈希值（秒传去重）
     */
    private String md5Hash;
    /**
     * 上传者ID
     */
    private Long uploadUserId;
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
