package com.zhan.qingzhiplatform.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResourceDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String description;
    private String course;
    @NotNull(message = "文件ID不能为空")
    private Long fileId;
}
