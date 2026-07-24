package com.zhan.qingzhiplatform.service;

import com.zhan.qingzhiplatform.pojo.PageResult;
import com.zhan.qingzhiplatform.pojo.entity.ResourceEntity;
import com.zhan.qingzhiplatform.pojo.dto.ResourceDTO;

public interface ResourceService {

    ResourceEntity publishResource(ResourceDTO dto, Long userId);
    ResourceEntity updateResource(Long id, ResourceDTO dto, Long userId);
    void deleteResource(Long id, Long userId);
    void deleteByAdmin(Long id);
    ResourceEntity getResourceById(Long id);
    PageResult<ResourceEntity> listResources(String start, String end, Integer status, Long userId, boolean isAdmin, Integer page, Integer pageSize);
    void auditResource(Long id, Integer status, String reason);
}
