package com.zhan.qingzhiplatform.service.impl;

import com.zhan.qingzhiplatform.pojo.entity.FileEntity;
import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FileMapper;
import com.zhan.qingzhiplatform.mapper.ResourceMapper;
import com.zhan.qingzhiplatform.service.FileService;
import com.zhan.qingzhiplatform.util.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    // 注入yaml中配置的存储文件目标目录变量
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    // 允许的文件类型
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /**
     * 上传文件
     *
     * @param file 文件实体
     * @param userId 用户ID
     * @return 上传文件信息
     */
    @Override
    public FileEntity uploadFile(MultipartFile file, Long userId) {
        try {
            return uploadCore(file, userId);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }


    /**
     * 批量上传文件
     *
     * @param files 多个文件
     * @param userId 用户ID
     * @return 上传文件信息
     */
    @Override
    @Transactional
    public List<FileEntity> batchUploadFiles(List<MultipartFile> files, Long userId) {
        List<FileEntity> result = new ArrayList<>();
        List<Path> writtenFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                FileEntity entity = uploadCore(file, userId);
                result.add(entity);
                writtenFiles.add(Paths.get(entity.getFilePath()));
            }
        } catch (Exception e) {
            for (Path path : writtenFiles) {
                try { Files.deleteIfExists(path); } catch (IOException ignored) {}
            }
            throw new BusinessException("批量上传失败: " + e.getMessage());
        }
        return result;
    }



    /** 核心上传逻辑，供单文件和批量共用 */
    private FileEntity uploadCore(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) throw new BusinessException("文件为空");
        if (file.getSize() > 50 * 1024 * 1024) throw new BusinessException("文件大小不能超过50MB");
        if (!ALLOWED_TYPES.contains(file.getContentType())) throw new BusinessException("不支持的文件类型: " + file.getContentType());

        String md5 = Md5Utils.calculateMd5(file.getBytes());

        FileEntity existing = fileMapper.getByMd5(md5);
        if (existing != null) return existing;

        String originalName = file.getOriginalFilename();
        int dotIdx = originalName != null ? originalName.lastIndexOf('.') : -1;
        String ext = dotIdx >= 0 ? originalName.substring(dotIdx) : "";
        String storageName = UUID.randomUUID().toString() + ext;

        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);

        Path targetPath = dir.resolve(storageName);
        file.transferTo(targetPath);

        FileEntity entity = new FileEntity();
        entity.setOriginalName(originalName);
        entity.setStorageName(storageName);
        entity.setFilePath(targetPath.toString());
        entity.setFileSize(file.getSize());
        entity.setFileType(file.getContentType());
        entity.setMd5Hash(md5);
        entity.setUploadUserId(userId);

        try {
            fileMapper.insert(entity);
        } catch (Exception e) {
            Files.deleteIfExists(targetPath);
            throw e;
        }
        return entity;
    }




    /**
     * 查询文件详细信息
     *
     * @param id 文件ID
     * @return FileEntity
     */
    @Override
    public FileEntity getFileById(Long id) {
        FileEntity file = fileMapper.getById(id);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        return file;
    }

    /**
     * 获取当前用户有权查看的预览文件
     *
     * @param id 文件ID
     * @param userId 当前用户ID
     * @param isAdmin 当前用户是否为管理员
     * @return 文件信息
     */
    @Override
    public FileEntity getPreviewFile(Long id, Long userId, boolean isAdmin) {
        FileEntity file = getFileById(id);
        if (!isAdmin && !resourceMapper.existsPreviewableByFileId(id, userId)) {
            throw new BusinessException("无权预览该文件");
        }
        return file;
    }

    /**
     * 删除未被资源引用的文件
     *
     * @param id 文件ID
     * @param userId 当前用户ID
     * @param isAdmin 当前用户是否为管理员
     */
    @Override
    @Transactional
    public void deleteFile(Long id, Long userId, boolean isAdmin) {
        FileEntity file = getFileById(id);
        if (!isAdmin && !userId.equals(file.getUploadUserId())) {
            throw new BusinessException("只能删除自己上传的文件");
        }
        if (resourceMapper.existsByFileId(id)) {
            throw new BusinessException("文件已被资源引用，请先删除关联资源");
        }

        fileMapper.deleteById(id);
        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
        } catch (IOException e) {
            throw new BusinessException("磁盘文件删除失败，数据库操作已回滚");
        }
    }

}
