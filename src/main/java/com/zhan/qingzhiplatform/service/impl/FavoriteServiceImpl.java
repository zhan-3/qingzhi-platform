package com.zhan.qingzhiplatform.service.impl;

import com.zhan.qingzhiplatform.pojo.entity.FavoriteEntity;
import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FavoriteMapper;
import com.zhan.qingzhiplatform.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;


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
        if (favoriteMapper.exists(userId, resourceId)) {
            throw new BusinessException("已收藏");
        }
        FavoriteEntity f = new FavoriteEntity();
        f.setUserId(userId);
        f.setResourceId(resourceId);
        favoriteMapper.insert(f);
    }


    /**
     * 用户取消收藏
     *
     * @param userId 用户ID
     * @param resourceId 资源ID
     */
    @Override
    public void removeFavorite(Long userId, Long resourceId) {
        favoriteMapper.deleteByUserAndResource(userId, resourceId);
    }

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @return 收藏列表
     */
    @Override
    public List<Map<String, Object>> listFavorites(Long userId) {
        return favoriteMapper.getByUserId(userId).stream().map(f -> Map.<String, Object>of(
                "id", f.getId(),
                "resourceId", f.getResourceId(),
                "userId", f.getUserId(),
                "createdAt", f.getCreatedAt()
        )).toList();
    }
}
