package com.zhan.qingzhiplatform.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资源实体类
 * 状态: 0待审核 1已通过 2已拒绝
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEntity {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;

    /**
     * 资源ID
     */
    private Long id;
    /**
     * 资源标题
     */
    private String title;
    /**
     * 资源描述
     */
    private String description;
    /**
     * 所属课程
     */
    private String course;
    /**
     * 关联文件ID
     */
    private Long fileId;
    /**
     * 发布者ID
     */
    private Long userId;
    /**
     * 状态：0待审核 1已通过 2已拒绝
     */
    private Integer status;
    /**
     * 拒绝理由
     */
    private String rejectReason;
    /**
     * 发布时间
     */
    private LocalDateTime createdAt;
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    /**
     * 软删除时间；为空表示有效
     */
    @JsonIgnore
    private LocalDateTime deletedAt;
    /**
     * 文件名 (联表查询, 非数据库字段)
     */
    private String fileName;
    /**
     * 发布者姓名 (联表查询, 非数据库字段)
     */
    private String userName;
}
