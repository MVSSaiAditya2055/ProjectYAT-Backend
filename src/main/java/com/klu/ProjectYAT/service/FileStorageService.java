package com.klu.ProjectYAT.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FileStorageService {

    @Value("${file.upload.assignments-dir}")
    private String assignmentsDir;

    @Value("${file.upload.submissions-dir}")
    private String submissionsDir;

    @Value("${file.upload.videos-dir}")
    private String videosDir;

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @PostConstruct
    public void init() {
        try {
            logger.info("Assignment directory: {}", Paths.get(assignmentsDir).toAbsolutePath());
            logger.info("Submission directory: {}", Paths.get(submissionsDir).toAbsolutePath());
            logger.info("Videos directory: {}", Paths.get(videosDir).toAbsolutePath());
        } catch (RuntimeException e) {
            logger.error("Error resolving absolute paths for upload directories", e);
        }
    }

    // ── Educator uploads an assignment question file ──────────────────────────
    public String storeAssignment(MultipartFile file, long courseId) throws IOException {
        Path dir = Paths.get(assignmentsDir, String.valueOf(courseId));
        Files.createDirectories(dir);
        logger.info("Storage target directory: {}", dir.toAbsolutePath());

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        String storedName = UUID.randomUUID() + ext;

        Files.copy(file.getInputStream(), dir.resolve(storedName),
                StandardCopyOption.REPLACE_EXISTING);
        return storedName;
    }

    // ── Educator uploads a video file ─────────────────────────────────────────
    public String storeVideo(MultipartFile file, long courseId) throws IOException {
        Path dir = Paths.get(videosDir, String.valueOf(courseId));
        Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        String storedName = UUID.randomUUID() + ext;

        Files.copy(file.getInputStream(), dir.resolve(storedName),
                StandardCopyOption.REPLACE_EXISTING);
        return storedName;
    }

    // ── Student uploads a submission file ─────────────────────────────────────
    public String storeSubmission(MultipartFile file, long courseId, long studentId) throws IOException {
        Path dir = Paths.get(submissionsDir, String.valueOf(courseId), String.valueOf(studentId));
        Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        String storedName = UUID.randomUUID() + ext;

        Files.copy(file.getInputStream(), dir.resolve(storedName),
                StandardCopyOption.REPLACE_EXISTING);
        return storedName;
    }

    public void deleteSubmissionQuietly(long courseId, long studentId, String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Path filePath = Paths.get(submissionsDir, String.valueOf(courseId), String.valueOf(studentId), fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // Swallow cleanup errors to avoid masking the original failure path.
        }
    }

    // ── Load a stored assignment file as a Resource ───────────────────────────
    public Resource loadAssignment(long courseId, String fileName) throws MalformedURLException {
        Path filePath = Paths.get(assignmentsDir, String.valueOf(courseId), fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("Assignment file not found: " + fileName);
        }
        return resource;
    }

    // ── Load a stored submission file as a Resource ───────────────────────────
    public Resource loadSubmission(long courseId, long studentId, String fileName) throws MalformedURLException {
        Path filePath = Paths.get(submissionsDir, String.valueOf(courseId),
                String.valueOf(studentId), fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("Submission file not found: " + fileName);
        }
        return resource;
    }

    // ── Load a stored video file as a Resource ────────────────────────────────
    public Resource loadVideo(long courseId, String fileName) throws MalformedURLException {
        Path filePath = Paths.get(videosDir, String.valueOf(courseId), fileName);
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) {
            throw new RuntimeException("Video file not found: " + fileName);
        }
        return resource;
    }
}
