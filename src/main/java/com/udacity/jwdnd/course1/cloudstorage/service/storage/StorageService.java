package com.udacity.jwdnd.course1.cloudstorage.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * This interface contains the methods required for file uploading functionality.
 * Extracted from https://github.com/spring-guides/gs-uploading-files
 */
public interface StorageService {
    void init();

    void store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String fileName);

    Resource loadAsResource(String fileName);

    void deleteAll();
}
