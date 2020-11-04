package com.udacity.jwdnd.course1.cloudstorage.service;

import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.service.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {

    private final Logger logger = LoggerFactory.getLogger(StorageService.class);

    private final FileMapper fileMapper;

    //private final Path rootLocation;

    /*@Autowired
    public FileService(FileMapper fileMapper, StorageProperties storageProperties) {
        this.fileMapper = fileMapper;
        rootLocation = Paths.get(storageProperties.getLocation());
    }
    */

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public File getFile(Integer fileId) {
        return fileMapper.getFile(fileId);
    }

    public List<File> getAllFiles(Integer userId) {
        return fileMapper.getAllFiles(userId);
    }

    public boolean isFilenameAvailable(String fileName) {
        return fileMapper.getFileByUsername(fileName) == null;
    }

    public int createFile(MultipartFile file, Integer creatorUserId) {
        try {
            if (file != null) {
                byte[] data = file.getInputStream().readAllBytes();
                File newFile = new File(
                        null,
                        file.getOriginalFilename(),
                        file.getContentType(),
                        Long.toString(file.getSize()),
                        creatorUserId,
                        data
                        );
                return fileMapper.insertFile(newFile);
            }
        } catch (IOException e) {
            logger.error("Failed to upload file");
        }
        return -1;
    }

    public int deleteFile(Integer fileId) {
        return fileMapper.deleteFile(fileId);
    }

    // These below regard file uploading - not necessary for the scope of this program
    /*
    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("File is empty");
            }
            Path destinationFile = rootLocation.resolve(Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                // Security check
                throw new StorageException("Cannot save file outside designated directory");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            logger.error("Error storing file: " + e.getMessage());
            throw new StorageException("Could not store file");
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String fileName) {
        return null;
    }

    @Override
    public Resource loadAsResource(String fileName) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
    */
}
