package com.klu.ProjectYAT.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.klu.ProjectYAT.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
	Optional<Course> findByTitle(String title);
	void deleteByTitle(String title);
}
