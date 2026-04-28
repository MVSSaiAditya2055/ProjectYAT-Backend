package com.klu.ProjectYAT.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klu.ProjectYAT.model.Course;
import com.klu.ProjectYAT.repository.CourseRepository;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/all")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.ok(savedCourse);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Optional<Course> existingCourse = courseRepository.findById(id);
        if (!existingCourse.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Course courseToUpdate = existingCourse.get();
        if (course.getTitle() != null) courseToUpdate.setTitle(course.getTitle());
        if (course.getModules() != null) courseToUpdate.setModules(course.getModules());
        if (course.getAssignmentFileName() != null) courseToUpdate.setAssignmentFileName(course.getAssignmentFileName());
        if (course.getAssignmentFileType() != null) courseToUpdate.setAssignmentFileType(course.getAssignmentFileType());
        
        // Only update registered students if the value provided is non-negative and explicitly sent
        // (Assuming 0 might be a default value, we check if it's actually different)
        if (course.getRegisteredStudents() >= 0) {
            // If the incoming course object has a different student count, we can update it.
            // But we should be careful not to overwrite with 0 if it was meant to be preserved.
            // For now, only update if it's actually in the request (handled by partial update logic).
            courseToUpdate.setRegisteredStudents(course.getRegisteredStudents());
        }

        Course updatedCourse = courseRepository.save(courseToUpdate);
        return ResponseEntity.ok(updatedCourse);
    }

    @PatchMapping("/{id}/student-count")
    public ResponseEntity<Course> adjustStudentCount(@PathVariable Long id, @RequestParam int delta) {
        Optional<Course> existingCourse = courseRepository.findById(id);
        if (!existingCourse.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Course course = existingCourse.get();
        int updatedCount = Math.max(0, course.getRegisteredStudents() + delta);
        course.setRegisteredStudents(updatedCount);

        Course saved = courseRepository.save(course);
        return ResponseEntity.ok(saved);
    }
}
