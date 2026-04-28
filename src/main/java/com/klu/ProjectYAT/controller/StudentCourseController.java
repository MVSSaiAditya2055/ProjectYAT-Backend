package com.klu.ProjectYAT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.klu.ProjectYAT.service.StudentCourseService;
import com.klu.ProjectYAT.dto.StudentCourseDTO;
import com.klu.ProjectYAT.dto.UpdateMarksRequest;
import java.util.List;

@RestController
@RequestMapping("/api/student-courses")
public class StudentCourseController {

    @Autowired
    private StudentCourseService studentCourseService;

    // Get all enrollments
    @GetMapping("/all")
    public ResponseEntity<?> getAllEnrollments() {
        try {
            List<StudentCourseDTO> enrollments = studentCourseService.getAllEnrollments();
            return ResponseEntity.ok(enrollments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollStudent(@RequestParam Long studentId, @RequestParam Long courseId) {
        try {
            StudentCourseDTO enrollment = studentCourseService.enrollStudent(studentId, courseId);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getStudentsInCourse(@PathVariable Long courseId) {
        try {
            List<StudentCourseDTO> students = studentCourseService.getStudentsInCourse(courseId);
            return ResponseEntity.ok(students);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getCoursesForStudent(@PathVariable Long studentId) {
        try {
            List<StudentCourseDTO> courses = studentCourseService.getCoursesForStudent(studentId);
            return ResponseEntity.ok(courses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/update-marks/{studentId}/{courseId}")
    public ResponseEntity<?> updateMarks(
            @PathVariable Long studentId,
            @PathVariable Long courseId,
            @RequestBody UpdateMarksRequest request) {
        try {
            StudentCourseDTO updatedEnrollment = studentCourseService.updateMarks(
                    studentId,
                    courseId,
                    request.getMarks(),
                    request.getFeedback());
            return ResponseEntity.ok(updatedEnrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<?> getEnrollment(@PathVariable Long enrollmentId) {
        try {
            StudentCourseDTO enrollment = studentCourseService.getEnrollmentById(enrollmentId);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<?> deleteEnrollment(@PathVariable Long enrollmentId) {
        try {
            studentCourseService.deleteEnrollment(enrollmentId);
            return ResponseEntity.ok(new SuccessResponse("Enrollment deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }

    public static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
