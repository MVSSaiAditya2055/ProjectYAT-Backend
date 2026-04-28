package com.klu.ProjectYAT.controller;

import com.klu.ProjectYAT.model.Course;
import com.klu.ProjectYAT.model.StudentCourse;
import com.klu.ProjectYAT.repository.CourseRepository;
import com.klu.ProjectYAT.repository.StudentCourseRepository;
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
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @PostMapping("/upload/{courseId}")
    public ResponseEntity<Map<String, Object>> uploadAssignment(
            @PathVariable long courseId,
            @RequestParam("file") MultipartFile file) {

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            String storedName = fileStorageService.storeAssignment(file, courseId);

            String relativePath = "/assignments/" + courseId + "/" + storedName;

            Course course = courseOpt.get();
            course.setAssignmentFileName(storedName);
            course.setAssignmentFileType(file.getContentType());
            courseRepository.save(course);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Assignment uploaded successfully");
            resp.put("fileName", storedName);
            resp.put("originalName", file.getOriginalFilename());
            resp.put("filePath", relativePath);
            resp.put("courseId", courseId);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Failed to store assignment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(err);
        }
    }

    @GetMapping("/download/{courseId}/{fileName}")
    public ResponseEntity<Resource> downloadAssignment(
            @PathVariable long courseId,
            @PathVariable String fileName) {

        try {
            Resource resource = fileStorageService.loadAssignment(courseId, fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/submit/{courseId}/{studentId}")
    public ResponseEntity<Map<String, Object>> submitAssignment(
            @PathVariable long courseId,
            @PathVariable long studentId,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Submission file is required");
            return ResponseEntity.badRequest().body(err);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !(originalFilename.toLowerCase().endsWith(".pdf") || 
                                           originalFilename.toLowerCase().endsWith(".doc") || 
                                           originalFilename.toLowerCase().endsWith(".docx"))) {
            Map<String, Object> err = new HashMap<>();
            err.put("error", "Unsupported file format. Only PDF (.pdf) and Word documents (.doc, .docx) are allowed.");
            return ResponseEntity.badRequest().body(err);
        }

        String storedName = null;

        try {
            Optional<StudentCourse> enrollmentOpt = studentCourseRepository.findByStudent_IdAndCourse_Id(studentId,
                    courseId);

            if (enrollmentOpt.isEmpty()) {
                Map<String, Object> err = new HashMap<>();
                err.put("error", "Enrollment not found for studentId=" + studentId + " and courseId=" + courseId);
                return ResponseEntity.status(404).body(err);
            }

            storedName = fileStorageService.storeSubmission(file, courseId, studentId);

            // Relative path that will be persisted in student_course.submission_file_path
            String relativePath = "/submissions/" + courseId + "/" + studentId + "/" + storedName;

            // Stamp the path into the student_course row
            StudentCourse enrollment = enrollmentOpt.get();
            enrollment.setSubmissionFilePath(relativePath);
            studentCourseRepository.saveAndFlush(enrollment);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Submission uploaded successfully");
            resp.put("fileName", storedName);
            resp.put("originalName", file.getOriginalFilename());
            resp.put("filePath", relativePath);
            resp.put("courseId", courseId);
            resp.put("studentId", studentId);
            return ResponseEntity.ok(resp);

        } catch (Throwable t) {
            fileStorageService.deleteSubmissionQuietly(courseId, studentId, storedName);

            Map<String, Object> err = new HashMap<>();
            String msg = t.getMessage() != null ? t.getMessage() : t.getClass().getSimpleName();
            err.put("error", "Failed to store submission: " + msg);
            t.printStackTrace();
            return ResponseEntity.internalServerError().body(err);
        }
    }

    @GetMapping("/submission/{courseId}/{studentId}/{fileName}")
    public ResponseEntity<Resource> downloadSubmission(
            @PathVariable long courseId,
            @PathVariable long studentId,
            @PathVariable String fileName) {

        try {
            Resource resource = fileStorageService.loadSubmission(courseId, studentId, fileName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
