package com.zhan.qingzhiplatform.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhan.qingzhiplatform.pojo.PageResult;
import com.zhan.qingzhiplatform.pojo.entity.FavoriteEntity;
import com.zhan.qingzhiplatform.pojo.entity.ResourceEntity;
import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FavoriteMapper;
import com.zhan.qingzhiplatform.mapper.ResourceMapper;
import com.zhan.qingzhiplatform.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ResourceMapper resourceMapper;


    /**
     * 用户添加收藏
     *
     * @param userId 用户ID
     * @param resourceId 资源ID
     */
    @Override
    public boolean isFavorited(Long userId, Long resourceId) {
        return favoriteMapper.exists(userId, resourceId);
    }

    @Override
    public void addFavorite(Long userId, Long resourceId) {
        ResourceEntity resource = resourceMapper.getById(resourceId);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        if (resource.getStatus() != ResourceEntity.STATUS_APPROVED) {
            throw new BusinessException("只能收藏已通过的资源");
        }
        if (favoriteMapper.exists(userId, resourceId)) {
            throw new BusinessException("已收藏");
        }
        if (favoriteMapper.restore(userId, resourceId) == 1) {
            return;
        }

        FavoriteEntity f = new FavoriteEntity();
        f.setUserId(userId);
        f.setResourceId(resourceId);
        try {
            favoriteMapper.insert(f);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("已收藏");
        }
    }


    /**
     * 用户取消收藏
     *
     * @param userId 用户ID
     * @param resourceId 资源ID
     */
    @Override
    public void removeFavorite(Long userId, Long resourceId) {
        favoriteMapper.softDeleteByUserAndResource(userId, resourceId);
    }

    /**
     * 分页获取用户收藏列表
     *
     * @param userId 用户ID
     * @param page 当前页码
     * @param pageSize 每页条数
     * @return 收藏列表
     */
    @Override
    public PageResult<Map<String, Object>> listFavorites(Long userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        PageInfo<FavoriteEntity> info = new PageInfo<>(favoriteMapper.getByUserId(userId));
        List<Map<String, Object>> records = info.getList().stream().map(f -> Map.<String, Object>of(
                "id", f.getId(),
                "resourceId", f.getResourceId(),
                "userId", f.getUserId(),
                "createdAt", f.getCreatedAt()
        )).toList();
        return new PageResult<>(records, info.getTotal(), page, pageSize);
    }
}
