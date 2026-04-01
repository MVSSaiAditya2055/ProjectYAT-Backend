package com.klu.ProjectYAT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.klu.ProjectYAT.model.StudentCourse;
import com.klu.ProjectYAT.model.User;
import com.klu.ProjectYAT.model.Course;
import com.klu.ProjectYAT.repository.StudentCourseRepository;
import com.klu.ProjectYAT.repository.UserRepository;
import com.klu.ProjectYAT.repository.CourseRepository;
import com.klu.ProjectYAT.dto.StudentCourseDTO;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentCourseService {

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Enroll a student in a course
    @Transactional
    public StudentCourse enrollStudent(Long studentId, Long courseId) {
        Optional<User> student = userRepository.findById(studentId);
        Optional<Course> course = courseRepository.findById(courseId);

        if (student.isEmpty() || course.isEmpty()) {
            throw new RuntimeException("Student or Course not found");
        }

        // Check if already enrolled
        Optional<StudentCourse> existingEnrollment = studentCourseRepository
            .findByStudentAndCourse(student.get(), course.get());
        
        if (existingEnrollment.isPresent()) {
            throw new RuntimeException("Student already enrolled in this course");
        }

        StudentCourse studentCourse = new StudentCourse(student.get(), course.get());
        return studentCourseRepository.save(studentCourse);
    }

    // Get all students in a course
    public List<StudentCourseDTO> getStudentsInCourse(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new RuntimeException("Course not found");
        }

        List<StudentCourse> enrollments = studentCourseRepository.findByCourseId(courseId);
        return enrollments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // Get all courses for a student
    public List<StudentCourseDTO> getCoursesForStudent(Long studentId) {
        Optional<User> student = userRepository.findById(studentId);
        if (student.isEmpty()) {
            throw new RuntimeException("Student not found");
        }

        List<StudentCourse> enrollments = studentCourseRepository.findByStudentId(studentId);
        return enrollments.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    // Update marks for a student in a course
    public StudentCourseDTO updateMarks(Long studentId, Long courseId, int marks, String feedback) {
        Optional<User> student = userRepository.findById(studentId);
        Optional<Course> course = courseRepository.findById(courseId);

        if (student.isEmpty() || course.isEmpty()) {
            throw new RuntimeException("Student or Course not found");
        }

        Optional<StudentCourse> enrollment = studentCourseRepository
            .findByStudentAndCourse(student.get(), course.get());

        if (enrollment.isEmpty()) {
            throw new RuntimeException("Student not enrolled in this course");
        }

        StudentCourse studentCourse = enrollment.get();
        studentCourse.setMarks(marks);
        if (feedback != null) {
            studentCourse.setFeedback(feedback);
        }

        StudentCourse updated = studentCourseRepository.save(studentCourse);
        return convertToDTO(updated);
    }

    // Get enrollment by ID
    public StudentCourseDTO getEnrollmentById(Long enrollmentId) {
        Optional<StudentCourse> enrollment = studentCourseRepository.findById(enrollmentId);
        if (enrollment.isEmpty()) {
            throw new RuntimeException("Enrollment not found");
        }
        return convertToDTO(enrollment.get());
    }

    @Transactional
    // Delete enrollment
    public void deleteEnrollment(Long enrollmentId) {
        if (!studentCourseRepository.existsById(enrollmentId)) {
            throw new RuntimeException("Enrollment not found");
        }
        studentCourseRepository.deleteById(enrollmentId);
    }

    // Convert StudentCourse to DTO
    private StudentCourseDTO convertToDTO(StudentCourse studentCourse) {
        return new StudentCourseDTO(
            studentCourse.getId(),
            studentCourse.getStudent().getId(),
            studentCourse.getStudent().getName(),
            studentCourse.getCourse().getId(),
            studentCourse.getCourse().getTitle(),
            studentCourse.getMarks(),
            studentCourse.getFeedback(),
            studentCourse.getEnrollmentDate()
        );
    }
}
