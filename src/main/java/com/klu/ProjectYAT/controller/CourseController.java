package com.klu.ProjectYAT.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.klu.ProjectYAT.model.Course;
import com.klu.ProjectYAT.repository.CourseRepository;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
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
