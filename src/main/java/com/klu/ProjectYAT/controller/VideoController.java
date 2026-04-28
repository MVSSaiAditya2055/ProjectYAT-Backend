package com.klu.ProjectYAT.controller;

import com.klu.ProjectYAT.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/videos")
public class VideoController {

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

        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Failed to store video: " + e.getMessage());
            e.printStackTrace();
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
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
