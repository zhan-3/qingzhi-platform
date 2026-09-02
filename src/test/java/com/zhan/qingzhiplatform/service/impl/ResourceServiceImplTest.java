package com.zhan.qingzhiplatform.service.impl;

import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FavoriteMapper;
import com.zhan.qingzhiplatform.mapper.FileMapper;
import com.zhan.qingzhiplatform.mapper.ResourceMapper;
import com.zhan.qingzhiplatform.pojo.entity.ResourceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private FileMapper fileMapper;

    @Mock
    private FavoriteMapper favoriteMapper;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void approvingResourceClearsPreviousRejectReason() {
        ResourceEntity resource = new ResourceEntity();
        resource.setId(1L);
        resource.setStatus(ResourceEntity.STATUS_REJECTED);
        resource.setRejectReason("资料不完整");
        when(resourceMapper.getById(1L)).thenReturn(resource);
        when(resourceMapper.updateAuditStatus(1L, ResourceEntity.STATUS_APPROVED, null)).thenReturn(1);

        resourceService.auditResource(1L, ResourceEntity.STATUS_APPROVED, "ignored");

        verify(resourceMapper).updateAuditStatus(1L, ResourceEntity.STATUS_APPROVED, null);
    }

    @Test
    void rejectingResourceRequiresReason() {
        BusinessException error = assertThrows(
                BusinessException.class,
                () -> resourceService.auditResource(1L, ResourceEntity.STATUS_REJECTED, "   ")
        );

        assertEquals("请填写拒绝原因", error.getMessage());
        verify(resourceMapper, never()).updateAuditStatus(1L, ResourceEntity.STATUS_REJECTED, null);
    }

    @Test
    void deletingResourceSoftDeletesFavoritesAndResource() {
        ResourceEntity resource = new ResourceEntity();
        resource.setId(1L);
        resource.setUserId(7L);
        when(resourceMapper.getById(1L)).thenReturn(resource);
        when(resourceMapper.softDeleteById(1L)).thenReturn(1);

        resourceService.deleteResource(1L, 7L);

        verify(favoriteMapper).softDeleteByResourceId(1L);
        verify(resourceMapper).softDeleteById(1L);
    }

    @Test
    void rejectingResourceTrimsReason() {
        ResourceEntity resource = new ResourceEntity();
        resource.setId(1L);
        when(resourceMapper.getById(1L)).thenReturn(resource);
        when(resourceMapper.updateAuditStatus(1L, ResourceEntity.STATUS_REJECTED, "资料不完整")).thenReturn(1);

        resourceService.auditResource(1L, ResourceEntity.STATUS_REJECTED, "  资料不完整  ");

        verify(resourceMapper).updateAuditStatus(1L, ResourceEntity.STATUS_REJECTED, "资料不完整");
    }
}
