package com.club69.mediaconvert.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class FileUtils {
    @Value("${temp.directory:tmp}")
    private String tempDirectory;

    public String createTempDirectory() throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        return createTempDirectory(uniqueId);
    }

    public String createTempDirectory(String uniqueId) throws IOException {
        Path tempPath = Paths.get(tempDirectory, uniqueId);
        Files.createDirectories(tempPath);

        log.debug("Created temp directory: {}", tempPath);
        return tempPath.toString();
    }

    public void cleanupTempDirectory(String tempDir) {
        try {
            Path tempPath = Paths.get(tempDir);
            if (Files.exists(tempPath)) {
                Files.walk(tempPath)
                        .sorted((a, b) -> b.compareTo(a))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                log.warn("Failed to delete temporary file: {}", path, e);
                            }
                        });
            }
        } catch (Exception e) {
            log.warn("Failed to cleanup temporary directory: {}", tempDir, e);
        }
    }
}
