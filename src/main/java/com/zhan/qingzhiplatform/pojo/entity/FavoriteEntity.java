package com.zhan.qingzhiplatform.pojo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteEntity {
    /**
     * 收藏ID
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 资源ID
     */
    private Long resourceId;
    /**
     * 收藏时间
     */
    private LocalDateTime createdAt;
    /**
     * 软删除时间；为空表示有效
     */
    @JsonIgnore
    private LocalDateTime deletedAt;
}
