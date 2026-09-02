package com.zhan.qingzhiplatform.service.impl;

import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FavoriteMapper;
import com.zhan.qingzhiplatform.mapper.ResourceMapper;
import com.zhan.qingzhiplatform.pojo.entity.FavoriteEntity;
import com.zhan.qingzhiplatform.pojo.entity.ResourceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void cannotFavoriteMissingResource() {
        when(resourceMapper.getById(99L)).thenReturn(null);

        BusinessException error = assertThrows(
                BusinessException.class,
                () -> favoriteService.addFavorite(1L, 99L)
        );

        assertEquals("资源不存在", error.getMessage());
        verify(favoriteMapper, never()).insert(any());
    }

    @Test
    void cannotFavoriteUnapprovedResource() {
        ResourceEntity resource = resourceWithStatus(ResourceEntity.STATUS_PENDING);
        when(resourceMapper.getById(2L)).thenReturn(resource);

        BusinessException error = assertThrows(
                BusinessException.class,
                () -> favoriteService.addFavorite(1L, 2L)
        );

        assertEquals("只能收藏已通过的资源", error.getMessage());
        verify(favoriteMapper, never()).insert(any());
    }

    @Test
    void canFavoriteApprovedResource() {
        ResourceEntity resource = resourceWithStatus(ResourceEntity.STATUS_APPROVED);
        when(resourceMapper.getById(2L)).thenReturn(resource);
        when(favoriteMapper.exists(1L, 2L)).thenReturn(false);

        favoriteService.addFavorite(1L, 2L);

        verify(favoriteMapper).insert(any(FavoriteEntity.class));
    }

    @Test
    void restoresSoftDeletedFavoriteInsteadOfInsertingDuplicatePair() {
        ResourceEntity resource = resourceWithStatus(ResourceEntity.STATUS_APPROVED);
        when(resourceMapper.getById(2L)).thenReturn(resource);
        when(favoriteMapper.exists(1L, 2L)).thenReturn(false);
        when(favoriteMapper.restore(1L, 2L)).thenReturn(1);

        favoriteService.addFavorite(1L, 2L);

        verify(favoriteMapper).restore(1L, 2L);
        verify(favoriteMapper, never()).insert(any());
    }

    @Test
    void duplicateKeyRaceReturnsFriendlyMessage() {
        ResourceEntity resource = resourceWithStatus(ResourceEntity.STATUS_APPROVED);
        when(resourceMapper.getById(2L)).thenReturn(resource);
        when(favoriteMapper.exists(1L, 2L)).thenReturn(false);
        doThrow(new DuplicateKeyException("duplicate"))
                .when(favoriteMapper).insert(any(FavoriteEntity.class));

        BusinessException error = assertThrows(
                BusinessException.class,
                () -> favoriteService.addFavorite(1L, 2L)
        );

        assertEquals("已收藏", error.getMessage());
    }

    private ResourceEntity resourceWithStatus(int status) {
        ResourceEntity resource = new ResourceEntity();
        resource.setId(2L);
        resource.setStatus(status);
        return resource;
    }
}
