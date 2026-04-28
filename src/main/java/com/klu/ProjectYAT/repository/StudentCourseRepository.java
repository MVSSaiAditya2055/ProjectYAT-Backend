package com.klu.ProjectYAT.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.klu.ProjectYAT.model.StudentCourse;
import com.klu.ProjectYAT.model.User;
import com.klu.ProjectYAT.model.Course;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    
    // Find all enrollments for a specific course
    List<StudentCourse> findByCourse(Course course);
    
    // Find all enrollments for a specific student
    List<StudentCourse> findByStudent(User student);
    
    // Find specific enrollment
    Optional<StudentCourse> findByStudentAndCourse(User student, Course course);
    
    // Find all enrollments for a course by course ID
    List<StudentCourse> findByCourse_Id(Long courseId);
    
    // Find all enrollments for a student by student ID
    List<StudentCourse> findByStudent_Id(Long studentId);

    // Find specific enrollment by student ID and course ID
    Optional<StudentCourse> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);
}
