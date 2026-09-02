package com.zhan.qingzhiplatform.service;

import com.zhan.qingzhiplatform.pojo.PageResult;

import java.util.Map;

public interface FavoriteService {

    void addFavorite(Long userId, Long resourceId);
    void removeFavorite(Long userId, Long resourceId);
    boolean isFavorited(Long userId, Long resourceId);
    PageResult<Map<String, Object>> listFavorites(Long userId, Integer page, Integer pageSize);
}
