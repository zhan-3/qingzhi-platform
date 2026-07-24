package com.zhan.qingzhiplatform.service;

import java.util.List;
import java.util.Map;

public interface FavoriteService {

    void addFavorite(Long userId, Long resourceId);
    void removeFavorite(Long userId, Long resourceId);
    boolean isFavorited(Long userId, Long resourceId);
    List<Map<String, Object>> listFavorites(Long userId);
}
