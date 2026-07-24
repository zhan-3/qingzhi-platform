package com.zhan.qingzhiplatform.controller;

import com.zhan.qingzhiplatform.pojo.entity.FileEntity;
import com.zhan.qingzhiplatform.pojo.Result;
import com.zhan.qingzhiplatform.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Slf4j
@RestController
@Tag(name = "文件管理", description = "文件相关接口")
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param file 文件实体
     * @param userId 上传用户ID
     * @return 上传结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件")
    public Result uploadFile(
            @RequestParam("file")
            @Parameter(description = "上传的文件", required = true)
            MultipartFile file,
            @Parameter(hidden = true) @RequestAttribute Long userId) {
        log.info("上传文件: userId={}, name={}, size={}", userId, file.getOriginalFilename(), file.getSize());
        return Result.success(fileService.uploadFile(file, userId));
    }

    /**
     * 批量上传文件
     *
     * @param files 多个文件
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量上传文件")
    public Result batchUploadFiles(
            @RequestPart("files")
            @Parameter(description = "多个文件")
            List<MultipartFile> files,
            @Parameter(hidden = true) @RequestAttribute Long userId) {
        return Result.success(fileService.batchUploadFiles(files, userId));
    }

    @GetMapping("/{id}/preview")
    @Operation(summary = "在线预览文件")
    public ResponseEntity<Resource> preview(@PathVariable Long id) {
        FileEntity file = fileService.getFileById(id);
        Path path = Paths.get(file.getFilePath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=" + URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8))
                .body(new FileSystemResource(path));
    }
}
