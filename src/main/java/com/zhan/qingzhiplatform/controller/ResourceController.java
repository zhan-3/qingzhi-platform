package com.zhan.qingzhiplatform.controller;

import com.zhan.qingzhiplatform.pojo.dto.ResourceDTO;
import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "用户资源管理", description = "用户资源管理相关的接口")
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;


    /**
     * 用户上传资源
     *
     * @param dto 资源实体
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping
    @Operation(summary = "用户上传资源")
    public Result publishResource(@Valid @RequestBody ResourceDTO dto, @RequestAttribute Long userId) {
        log.info("发布资源: userId={}, title={}", userId, dto.getTitle());
        return Result.success(resourceService.publishResource(dto, userId));
    }

    /**
     * 更新自己上传的资源
     *
     * @param id 资源ID
     * @param dto 新的资源DTO
     * @param userId 用户ID
     * @return 更新结果
     */
    @Operation(summary = "更新资源")
    @PutMapping("/{id}")
    public Result updateResource(@PathVariable Long id, @RequestBody ResourceDTO dto, @RequestAttribute Long userId) {
        return Result.success(resourceService.updateResource(id, dto, userId));
    }

    /**
     * 删除自己上传的资源
     *
     * @param id 资源ID
     * @param userId 用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除资源")
    @DeleteMapping("/{id}")
    public Result deleteResource(@PathVariable Long id, @RequestAttribute Long userId) {
        resourceService.deleteResource(id, userId);
        return Result.success("删除成功");
    }

    /**
     * 查询资源信息
     *
     * @param id 资源ID
     * @return 查询结果
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取资源详情")
    public Result getResourceById(@PathVariable Long id) {
        return Result.success(resourceService.getResourceById(id));
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
     * @param role 角色
     * @return 查询结果
     */
    @GetMapping
    @Operation(summary = "分页查询资源")
    public Result listResources(@RequestParam(required = false) String begin,
                       @RequestParam(required = false) String end,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @RequestAttribute Long userId,
                       @RequestAttribute Integer role) {
        boolean isAdmin = role != null && role == 2;
        return Result.success(resourceService.listResources(begin, end, status, userId, isAdmin, page, pageSize));
    }
}
