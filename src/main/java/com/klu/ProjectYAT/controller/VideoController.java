package com.klu.ProjectYAT.controller;

import com.klu.ProjectYAT.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload/{courseId}")
    public ResponseEntity<Map<String, Object>> uploadVideo(
            @PathVariable long courseId,
            @RequestParam("file") MultipartFile file) {

        try {
            String storedName = fileStorageService.storeVideo(file, courseId);

            String streamUrl = "/api/videos/stream/" + courseId + "/" + storedName;

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Video uploaded successfully");
            resp.put("fileName", storedName);
            resp.put("videoUrl", streamUrl);
            return ResponseEntity.ok(resp);

        } catch (IOException | RuntimeException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Failed to store video: " + e.getMessage());
            logger.error("Failed to store video for course {}", courseId, e);
            return ResponseEntity.internalServerError().body(err);
        }
    }

    @GetMapping("/stream/{courseId}/{fileName}")
    public ResponseEntity<Resource> streamVideo(
            @PathVariable long courseId,
            @PathVariable String fileName) {

        try {
            Resource resource = fileStorageService.loadVideo(courseId, fileName);
            String contentType = "video/mp4"; // Default fallback

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException | RuntimeException e) {
            logger.warn("Video not available for course {} and file {}", courseId, fileName, e);
            return ResponseEntity.notFound().build();
        }
    }
}
