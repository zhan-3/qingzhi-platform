package com.zhan.qingzhiplatform.controller;

import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "我的收藏", description = "收藏相关的接口")
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     *
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 添加收藏结果
     */
    @PostMapping("/{resourceId}")
    @Operation(summary = "添加收藏资源")
    public Result addFavorite(@PathVariable Long resourceId, @RequestAttribute Long userId) {
        log.info("收藏资源: userId={}, resourceId={}", userId, resourceId);
        favoriteService.addFavorite(userId, resourceId);
        return Result.success("收藏成功");
    }

    /**
     * 取消收藏
     *
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 取消收藏结果
     */
    @DeleteMapping("/{resourceId}")
    @Operation(summary = "取消收藏资源")
    public Result removeFavorite(@PathVariable Long resourceId, @RequestAttribute Long userId) {
        favoriteService.removeFavorite(userId, resourceId);
        return Result.success("取消收藏");
    }

    /**
     * 查询个人收藏列表
     *
     * @param userId 用户个人ID
     * @return 个人收藏列表
     */
    @GetMapping
    @Operation(summary = "查询个人收藏列表")
    public Result listFavorites(@RequestAttribute Long userId) {
        return Result.success(favoriteService.listFavorites(userId));
    }
}
