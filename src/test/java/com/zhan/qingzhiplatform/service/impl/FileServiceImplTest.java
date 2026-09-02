package com.zhan.qingzhiplatform.service.impl;

import com.zhan.qingzhiplatform.exception.BusinessException;
import com.zhan.qingzhiplatform.mapper.FileMapper;
import com.zhan.qingzhiplatform.mapper.ResourceMapper;
import com.zhan.qingzhiplatform.pojo.entity.FileEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private ResourceMapper resourceMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    @TempDir
    Path tempDir;

    @Test
    void userCanPreviewFileReferencedByVisibleResource() {
        FileEntity file = file(10L, 2L, null);
        when(fileMapper.getById(10L)).thenReturn(file);
        when(resourceMapper.existsPreviewableByFileId(10L, 2L)).thenReturn(true);

        FileEntity result = fileService.getPreviewFile(10L, 2L, false);

        assertSame(file, result);
    }

    @Test
    void userCannotPreviewFileWithoutVisibleResource() {
        FileEntity file = file(10L, 2L, null);
        when(fileMapper.getById(10L)).thenReturn(file);
        when(resourceMapper.existsPreviewableByFileId(10L, 2L)).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> fileService.getPreviewFile(10L, 2L, false));
    }

    @Test
    void adminCanPreviewExistingFileWithoutResourceLookup() {
        FileEntity file = file(10L, 2L, null);
        when(fileMapper.getById(10L)).thenReturn(file);

        FileEntity result = fileService.getPreviewFile(10L, 1L, true);

        assertSame(file, result);
        verify(resourceMapper, never()).existsPreviewableByFileId(10L, 1L);
    }

    @Test
    void referencedFileCannotBeDeleted() {
        FileEntity file = file(10L, 2L, tempDir.resolve("stored.pdf"));
        when(fileMapper.getById(10L)).thenReturn(file);
        when(resourceMapper.existsByFileId(10L)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> fileService.deleteFile(10L, 2L, false));

        verify(fileMapper, never()).deleteById(10L);
    }

    @Test
    void userCannotDeleteAnotherUsersFile() {
        FileEntity file = file(10L, 2L, tempDir.resolve("stored.pdf"));
        when(fileMapper.getById(10L)).thenReturn(file);

        assertThrows(BusinessException.class,
                () -> fileService.deleteFile(10L, 3L, false));

        verify(resourceMapper, never()).existsByFileId(10L);
        verify(fileMapper, never()).deleteById(10L);
    }

    @Test
    void unreferencedFileIsDeletedFromDatabaseAndDisk() throws IOException {
        Path storedFile = Files.createFile(tempDir.resolve("stored.pdf"));
        FileEntity file = file(10L, 2L, storedFile);
        when(fileMapper.getById(10L)).thenReturn(file);
        when(resourceMapper.existsByFileId(10L)).thenReturn(false);

        fileService.deleteFile(10L, 2L, false);

        verify(fileMapper).deleteById(10L);
        assertFalse(Files.exists(storedFile));
    }

    private FileEntity file(Long id, Long uploadUserId, Path path) {
        FileEntity file = new FileEntity();
        file.setId(id);
        file.setUploadUserId(uploadUserId);
        if (path != null) {
            file.setFilePath(path.toString());
        }
        return file;
    }
}
