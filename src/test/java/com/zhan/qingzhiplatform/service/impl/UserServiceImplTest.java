package com.zhan.qingzhiplatform.service.impl;

import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FavoriteMapper;
import com.zhan.qingzhiplatform.mapper.ResourceMapper;
import com.zhan.qingzhiplatform.mapper.UserMapper;
import com.zhan.qingzhiplatform.pojo.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void deletingUserSoftDeletesAllOwnedRelations() {
        UserEntity user = new UserEntity();
        user.setId(7L);
        user.setRole(0);
        when(userMapper.getById(7L)).thenReturn(user);
        when(userMapper.softDeleteById(7L)).thenReturn(1);

        userService.deleteUser(7L);

        InOrder order = inOrder(favoriteMapper, resourceMapper, userMapper);
        order.verify(favoriteMapper).softDeleteByResourceOwner(7L);
        order.verify(favoriteMapper).softDeleteByUserId(7L);
        order.verify(resourceMapper).softDeleteByUserId(7L);
        order.verify(userMapper).softDeleteById(7L);
    }

    @Test
    void cannotDeleteAdministrator() {
        UserEntity admin = new UserEntity();
        admin.setId(1L);
        admin.setRole(2);
        when(userMapper.getById(1L)).thenReturn(admin);

        BusinessException error = assertThrows(
                BusinessException.class,
                () -> userService.deleteUser(1L)
        );

        assertEquals("不能删除管理员账号", error.getMessage());
        verify(userMapper, never()).softDeleteById(1L);
    }
}
