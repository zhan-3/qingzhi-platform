package com.zhan.qingzhiplatform.controller;

import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.pojo.dto.AuditDTO;
import com.zhan.qingzhiplatform.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "管理员资源管理")
@RequestMapping("/admin/resources")
public class AdminResourceController {

    @Autowired
    private ResourceService resourceService;

    /**
     * 管理员审核资源
     *
     * @param id 资源ID
     * @param dto 审核结果DTO
     * @return 审核结果
     */
    @PutMapping("/{id}/audit")
    @Operation(summary = "审核资源")
    public Result auditResource(@PathVariable Long id, @Valid @RequestBody AuditDTO dto) {
        log.info("审核资源: id={}, status={}", id, dto.getStatus());
        resourceService.auditResource(id, dto.getStatus(), dto.getReason());
        return Result.success("审核完成");
    }

    /**
     * 管理员删除资源
     *
     * @param id 资源ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除任意资源")
    public Result deleteResource(@PathVariable Long id) {
        resourceService.deleteByAdmin(id);
        return Result.success("删除成功");
    }

    /**
     * 管理员条件分页查询资源
     *
     * @param start 开始时间
     * @param end 结束时间
     * @param status 资源状态
     * @param page 当前页码
     * @param pageSize 每页显示条数
     */
    @GetMapping
    @Operation(summary = "条件分页查询资源")
    public Result listResources(@RequestParam(required = false) String start,
                      @RequestParam(required = false) String end,
                      @RequestParam(required = false) Integer status,
                      @RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(resourceService.listResources(start, end, status, 0L, true, page, pageSize));
    }
}
