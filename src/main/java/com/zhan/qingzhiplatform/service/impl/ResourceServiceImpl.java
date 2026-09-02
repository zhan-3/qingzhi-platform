package com.zhan.qingzhiplatform.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhan.qingzhiplatform.pojo.PageResult;
import com.zhan.qingzhiplatform.pojo.entity.ResourceEntity;
import com.zhan.qingzhiplatform.pojo.dto.ResourceDTO;
import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FavoriteMapper;
import com.zhan.qingzhiplatform.mapper.FileMapper;
import com.zhan.qingzhiplatform.mapper.ResourceMapper;
import com.zhan.qingzhiplatform.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    private void validateFileId(Long fileId) {
        if (fileId == null) throw new BusinessException("文件ID不能为空");
        if (fileMapper.getById(fileId) == null) throw new BusinessException("文件不存在");
    }

    /**
     * 上传资源
     *
     * @param dto 资源DTO
     * @param userId 用户ID
     * @return 上传的资源
     */
    @Override
    public ResourceEntity publishResource(ResourceDTO dto, Long userId) {
        validateFileId(dto.getFileId());
        ResourceEntity r = new ResourceEntity();
        r.setTitle(dto.getTitle());
        r.setDescription(dto.getDescription());
        r.setCourse(dto.getCourse());
        r.setFileId(dto.getFileId());
        r.setUserId(userId);
        r.setStatus(ResourceEntity.STATUS_PENDING);
        resourceMapper.insert(r);
        log.info("发布资源: userId={}, title={}", userId, dto.getTitle());
        return r;
    }

    /**
     * 更新自己上传的资源
     *
     * @param id 资源ID
     * @param dto 资源DTO
     * @param userId 用户ID
     * @return 更新后的资源
     */
    @Override
    public ResourceEntity updateResource(Long id, ResourceDTO dto, Long userId) {
        ResourceEntity r = resourceMapper.getById(id);
        if (r == null) throw new BusinessException("资源不存在");
        if (!r.getUserId().equals(userId)) throw new BusinessException("只能修改自己的资源");
        validateFileId(dto.getFileId());
        r.setTitle(dto.getTitle());
        r.setDescription(dto.getDescription());
        r.setCourse(dto.getCourse());
        r.setFileId(dto.getFileId());
        r.setStatus(ResourceEntity.STATUS_PENDING);
        resourceMapper.update(r);
        return r;
    }

    /**
     * 删除自己上传的资源
     *
     * @param id 资源ID
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void deleteResource(Long id, Long userId) {
        ResourceEntity r = resourceMapper.getById(id);
        if (r == null) throw new BusinessException("资源不存在");
        if (!r.getUserId().equals(userId)) throw new BusinessException("只能删除自己的资源");
        favoriteMapper.softDeleteByResourceId(id);
        if (resourceMapper.softDeleteById(id) != 1) {
            throw new BusinessException("资源删除失败");
        }
    }

    /**
     * 管理员删除任意资源
     *
     * @param id 资源ID
     */
    @Override
    @Transactional
    public void deleteByAdmin(Long id) {
        if (resourceMapper.getById(id) == null) throw new BusinessException("资源不存在");
        favoriteMapper.softDeleteByResourceId(id);
        if (resourceMapper.softDeleteById(id) != 1) {
            throw new BusinessException("资源删除失败");
        }
    }

    /**
     * 获取资源详细信息
     *
     * @param id 资源ID
     * @return 资源信息
     */
    @Override
    public ResourceEntity getResourceById(Long id) {
        ResourceEntity r = resourceMapper.getById(id);
        if (r == null) throw new BusinessException("资源不存在");
        return r;
    }

    /**
     * 管理员审核资源
     *
     * @param id 资源ID
     * @param status 资源状态
     * @param reason 拒绝原因
     */
    @Override
    public void auditResource(Long id, Integer status, String reason) {
        if (status != ResourceEntity.STATUS_APPROVED && status != ResourceEntity.STATUS_REJECTED)
            throw new BusinessException("审核状态无效");
        if (status == ResourceEntity.STATUS_REJECTED && (reason == null || reason.isBlank()))
            throw new BusinessException("请填写拒绝原因");
        ResourceEntity r = resourceMapper.getById(id);
        if (r == null) throw new BusinessException("资源不存在");
        String normalizedReason = status == ResourceEntity.STATUS_REJECTED ? reason.trim() : null;
        int affected = resourceMapper.updateAuditStatus(id, status, normalizedReason);
        if (affected != 1) throw new BusinessException("资源审核失败");
        log.info("审核资源: id={}, status={}", id, status);
    }

    /**
     * 条件分页查询资源
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @param status 资源状态
     * @param page  当前页面
     * @param pageSize 每页条数
     * @param userId 用户ID
     * @param isAdmin 是否管理员
     * @return 分页查询结果
     */
    @Override
    public PageResult<ResourceEntity> listResources(String begin, String end, Integer status, Long userId, boolean isAdmin, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        PageInfo<ResourceEntity> info = new PageInfo<>(resourceMapper.getResources(begin, end, status, userId, isAdmin));
        return new PageResult<>(info.getList(), info.getTotal(), page, pageSize);
    }
}
